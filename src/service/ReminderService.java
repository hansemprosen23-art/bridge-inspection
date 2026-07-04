package service;

import entity.Bridge;
import entity.BridgeInitialCheck;
import entity.BridgeRegularCheck;
import entity.CheckReminder;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 检查提醒服务
 * 根据 initial_check 和 regular_check 的 next_check_date 计算即将到期的检查计划
 */
public class ReminderService {

    private static ReminderService instance;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private ReminderService() {}

    public static synchronized ReminderService getInstance() {
        if (instance == null) instance = new ReminderService();
        return instance;
    }

    /**
     * 获取即将到期的检查提醒
     * @param daysThreshold 提前多少天提醒（默认30天）
     */
    public List<CheckReminder> getUpcomingChecks(int daysThreshold) {
        List<CheckReminder> reminders = new ArrayList<>();
        Date today = new Date();

        // 定期检查提醒
        List<BridgeRegularCheck> regularChecks = BridgeRegularCheckService.getInstance().getAllChecks();
        for (BridgeRegularCheck check : regularChecks) {
            if (check.getNextCheckDate() == null || check.getNextCheckDate().isEmpty()) continue;
            try {
                Date nextDate = sdf.parse(check.getNextCheckDate());
                int days = daysBetween(today, nextDate);
                if (days <= daysThreshold && days >= -30) { // 包含已过期30天内的
                    CheckReminder r = new CheckReminder();
                    r.setBridgeId(check.getBridgeId());
                    r.setBridgeName(check.getBridgeName());
                    r.setCheckType("定期检查");
                    r.setPlannedDate(nextDate);
                    r.setDaysRemaining(days);
                    r.setUrgency(getUrgency(days));
                    reminders.add(r);
                }
            } catch (Exception e) {
                // 忽略日期解析错误
            }
        }

        // 初始检查提醒
        List<BridgeInitialCheck> initialChecks = BridgeInitialCheckService.getInstance().getAllChecks();
        for (BridgeInitialCheck check : initialChecks) {
            if (check.getNextCheckDate() == null || check.getNextCheckDate().isEmpty()) continue;
            try {
                Date nextDate = sdf.parse(check.getNextCheckDate());
                int days = daysBetween(today, nextDate);
                if (days <= daysThreshold && days >= -30) {
                    CheckReminder r = new CheckReminder();
                    r.setBridgeId(check.getBridgeId());
                    Bridge bridge = BridgeService.getInstance().getBridgeById(check.getBridgeId());
                    r.setBridgeName(bridge != null ? bridge.getBridgeName() : "未知桥梁");
                    r.setCheckType("初始检查");
                    r.setPlannedDate(nextDate);
                    r.setDaysRemaining(days);
                    r.setUrgency(getUrgency(days));
                    reminders.add(r);
                }
            } catch (Exception e) {
                // 忽略
            }
        }

        // 按剩余天数升序排列
        reminders.sort(Comparator.comparingInt(CheckReminder::getDaysRemaining));
        return reminders;
    }

    private int daysBetween(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    private String getUrgency(int days) {
        if (days < 0) return "已过期";
        if (days <= 7) return "紧急";
        if (days <= 30) return "即将到期";
        return "正常";
    }
}
