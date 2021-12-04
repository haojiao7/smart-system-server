package org.jeecg.modules.smartJob.service;

import org.jeecg.modules.SmartPunishPeople.entity.SmartPunishPeople;
import org.jeecg.modules.smartJob.entity.SmartJob;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.smartJob.entity.SysUser;

import java.util.List;

public interface ISmartJobService extends IService<SmartJob> {

    boolean edit(SmartJob smartJob, String sendFrom);

    boolean openJob(SmartJob smartJob, String sendFrom);


    List<SmartPunishPeople> getPunish();

    List<SysUser> getAnniversaryList();

    List<SysUser> getAllUser();

    List<SysUser> selectBatchIds(String ids);

    List<SysUser> getUsers(List<String> userids);

    void updateStatus(String jobBean);

    List<SmartJob> initGetTasks();

    String getOrgId(String from);
}