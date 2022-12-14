## InIt

- Java Gradle
- Log4j2 사용
    - Console - logLevel (DEBUG)
    - FIle - logLevel(Info)
- 다중 쓰레드 방식
- 쓰레드 풀에 쓰레드가 없을 경우 misFire 발생 → 핸들링 필요
- 로컬 폴더를 원격(SFTP) EC2 서버로 주기적 백업
    - `jcraft` 사용

# 구동방식

## SFTP 백업 동작소스(jcraft)

[SFTP Logic](https://www.notion.so/SFTP-Logic-6e71e6313c41484bb0c86713514cfb1c)

## 구성도

![image](https://user-images.githubusercontent.com/49707719/206842441-a634e7cf-fe33-4e4a-ac57-8476a677512c.png)

## Job

- 실제 구동 인터페이스 제공
    - execute Override
    - 실제 SFTP 백업로직

```java
@Override
public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("## SFTP Job Call!!");
    // SFTP 정보 INIT
    run(context);
}

private void run(JobExecutionContext context) {
    host = context.getJobDetail().getJobDataMap().getString("host");
    port = context.getJobDetail().getJobDataMap().getInt("port");
    username = context.getJobDetail().getJobDataMap().getString("username");
    priKey = context.getJobDetail().getJobDataMap().getString("privateKey");
    sourcePath = context.getJobDetail().getJobDataMap().getString("sourcepath");
    uploadPath = context.getJobDetail().getJobDataMap().getString("uploadpath");
    log.info("## SFTP Job Call!! host : {}, port : {}, username : {}, priKey : {} sourcePath : {} uploadPath :{}"
            , host, port, username, priKey,sourcePath,uploadPath);

    SFTPControl control= new SFTPControl();
    control.init(host, username,"",port,priKey);

    try {
        control.recursiveFolderUpload(sourcePath,uploadPath);
    } catch (SftpException e) {
        log.error("SftpException : {}",e.getMessage());
        throw new RuntimeException(e);
    } catch (FileNotFoundException e) {
        log.error("FileNotFoundException : {}",e.getMessage());
        throw new RuntimeException(e);
    }
}
```

## JobDataMap → 이 정보 기반으로 스케쥴링

- Job 인스턴스 실행 시 원하는 정보를 담을 수 있게해주는 객체
- JobDetail
    - 삽입 속성
    
    | 속성명 | 기능 |
    | --- | --- |
    | host | EC2 IP |
    | port | SFTP Port |
    | username | 계정 |
    | privatekey | 개인키 |
    | uploadpath | 원격 경로 |
    | sourcepath | 로컬경로 |
    

## Trigger

- Job을 실행시킬 조건(주기 또는 횟수)
- 1 Trigger  = 1 Job  사용
- SimpleTrigger 사용
- MisFire 정책 설정을 여기서 함
    
    ```java
    public class TriggerMaker {
    
        Trigger trigger =newTrigger()
    						//소속 이름, 소속 그룹
                .withIdentity("HelloTrigger", "HelloGroup")
    						//트리거 작동 시 Job 바로실행
                .startNow()
                //simpleSchedule사용 -> 1분마다 활성화, 최대 3번 반복
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(1)
                        .withRepeatCount(3))
                .build();
    
        public Trigger getTrigger() {
            return trigger;
        }
    }
    ```
    
- 실제 사용 용도가 아니므로 실행횟수 제한

## JobListner

- 스케쥴러 이벤트를 받을 수 있도록 하는 인터페이스
- jobScheduler 사용
    - 작업시작, 작업중단, 작업종료 확인가능

```java
@Override
public String getName() {
    return MyJobListener.class.getName();
}

/**
 * Job이 수행되기 전 상태
* - TriggerListener.vetoJobExecution == false
 */
@Override
public void jobToBeExecuted(JobExecutionContext context) {
    logger.info(String.format("[%-18s][%s] 작업시작", "jobToBeExecuted", context.getJobDetail().getKey().toString()));
}

/**
 * Job이 중단된 상태
* - TriggerListener.vetoJobExecution == true
 */
@Override
public void jobExecutionVetoed(JobExecutionContext context) {
    logger.info(String.format("[%-18s][%s] 작업중단", "jobExecutionVetoed", context.getJobDetail().getKey().toString()));
}

/**
 * Job수행이 완료된 상태
*/
@Override
public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    logger.info(String.format("[%-18s][%s] 작업완료", "jobWasExecuted", context.getJobDetail().getKey().toString()));
}
```

## JobStore

- RAMJobStore 사용 → DB저장 필요없는 작업
    - 이슈 핸들링 필요 시, 로컬PC(스케쥴러 동작 서버)에 로그적재로 해결가능
    - Log4j2 사용
