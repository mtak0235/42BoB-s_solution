package bobs.Job;

import bobs.Dao.Class.JdbcRoomInfoDao;
import bobs.Dao.Class.JdbcRoomMatchDao;
import bobs.Slack.Slack;
import lombok.SneakyThrows;
import org.quartz.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@PersistJobDataAfterExecution
public class AlarmJob implements Job {

    private JdbcRoomInfoDao roomInfoDao;
    private JdbcRoomMatchDao roomMatchDao;
    private Slack Slack = new Slack();
    private JobDetailProducer jobDetailProducer;
    private JobTriggerProducer jobTriggerProducer;
    private Scheduler scheduler;

    public AlarmJob(JdbcRoomInfoDao roomInfoDao, JdbcRoomMatchDao roomMatchDao, JobDetailProducerImpl jobDetailProducer, JobTriggerProducerImpl jobTriggerPorducerImpl, Scheduler scheduler) {
        this.roomInfoDao = roomInfoDao;
        this.roomMatchDao = roomMatchDao;
        this.jobDetailProducer= jobDetailProducer;
        this.jobTriggerProducer = jobTriggerPorducerImpl;
        this.scheduler = scheduler;
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int ret_count = dataMap.getIntValue("ret_count");


        /*5회 까지 재시도*/
        if(ret_count > 4){
            JobExecutionException e = new JobExecutionException("Retries exceeded");
            /* 5회 재시도에도 오류가 계속 된다면 스케줄을 멈추고 다시 등록*/
            e.setUnscheduleAllTriggers(true);
            scheduler.scheduleJob(jobDetailProducer.getAlarmDetail(), jobTriggerProducer.getAlarmTrigger());
            /*여기에 로그를 남기면 좋을것 같은데..*/

            throw e;
        }

        try {
            List<String> roomIdList = roomInfoDao.getAlarmRoomId();
            if (roomIdList.isEmpty() == false) {
                Map<String, List<String>> alarmUserIdMap = roomMatchDao.getAlarmUserId(roomIdList);
                Set keyset = alarmUserIdMap.keySet();
                for (Object key : keyset) {
                    Slack.sendSuccessMsg(alarmUserIdMap.get(String.valueOf(key)));
                    roomInfoDao.roomStatusUpdate(Integer.parseInt(String.valueOf(key)), "succeed");
                }

            }
            // reset ret_count to 0.
            dataMap.putAsString("ret_count", 0);
        } catch (Exception e) {
            ret_count++;
            dataMap.putAsString("ret_count", ret_count);
            Thread.sleep(60000);
            JobExecutionException e2 = new JobExecutionException(e);
            e2.refireImmediately();
            throw e2;
        }
    }
}