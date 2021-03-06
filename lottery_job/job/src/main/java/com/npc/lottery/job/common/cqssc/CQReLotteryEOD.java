package com.npc.lottery.job.common.cqssc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.npc.lottery.common.Constant;
import com.npc.lottery.common.service.ShopSchemeService;
import com.npc.lottery.member.logic.interf.ILotteryResultLogic;
import com.npc.lottery.monitoring.entity.MonitoringInfo;
import com.npc.lottery.monitoring.logic.interf.IMonitoringInfoLogic;
import com.npc.lottery.periods.entity.CQPeriodsInfo;
import com.npc.lottery.periods.logic.interf.ICQPeriodsInfoLogic;

public class CQReLotteryEOD extends QuartzJobBean {

	static Logger logger = Logger.getLogger(CQReLotteryEOD.class);
	@Autowired
	private ICQPeriodsInfoLogic icqPeriodsInfoLogic;

	@Autowired
	private ILotteryResultLogic lotteryResultLogic;

	@Autowired
	private ShopSchemeService shopSchemeService;

	@Autowired
	private IMonitoringInfoLogic monitoringInfoLogic;

	/**
	 * 重慶時時彩重新兌獎
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("重庆时时彩  对投注表遗漏数据重新兑奖>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始");
		// 获取商铺对应的scheme列表
		Map<String, String> shopSchemeMap = shopSchemeService.getShopSchemeMap();
		for (Entry<String, String> shopScheme : shopSchemeMap.entrySet()) {
			long startTime = System.currentTimeMillis();
			String scheme = shopScheme.getValue();
			String shopCode = shopScheme.getKey();
			try {
				// 获取盘期表里已兑奖或开奖,但投注表里遗漏兑奖的盘期列表,key=盘期,value=盘期对象
				Map<String, CQPeriodsInfo> periodsInfoMap = icqPeriodsInfoLogic.getPeriodsInfoFromBetTableForReLotteryEOD(scheme);

				if (MapUtils.isNotEmpty(periodsInfoMap)) {

					// 如果获取的盘期列表，投注表里有没有兑奖的数据，执行该期重新兑奖的操作
					for (Map.Entry<String, CQPeriodsInfo> entry : periodsInfoMap.entrySet()) {
						CQPeriodsInfo periodsInfo = entry.getValue();
						logger.info("重庆时时彩  对投注表遗漏数据重新兑奖>>>>>>>>>>盘期：" + periodsInfo.getPeriodsNum());
						List<Integer> numsList = new ArrayList<Integer>();
						numsList.add(periodsInfo.getResult1());
						numsList.add(periodsInfo.getResult2());
						numsList.add(periodsInfo.getResult3());
						numsList.add(periodsInfo.getResult4());
						numsList.add(periodsInfo.getResult5());
						long star = System.currentTimeMillis();
						try {
							lotteryResultLogic.updatePlayTypeWin(periodsInfo.getPeriodsNum(), numsList, Constant.LOTTERY_TYPE_CQSSC);
							lotteryResultLogic.updateCQForReLottery(periodsInfo.getPeriodsNum(), numsList, periodsInfo.getOpenQuotTime(), scheme);
							monitoringInfoLogic.saveMonitoringInfo(new MonitoringInfo(0, "CQSSC", shopScheme.getKey(), periodsInfo.getPeriodsNum(), "重新兑奖", 1, "商铺：" + shopScheme.getKey() + "的重庆时时彩"
							        + periodsInfo.getPeriodsNum() + "盘期重新兑奖", (int) (System.currentTimeMillis() - star)));
							// 3.计算统计报表
							long startReport = System.currentTimeMillis();
							boolean flag = lotteryResultLogic.saveReportPeriod(periodsInfo.getPeriodsNum(), Constant.LOTTERY_TYPE_CQSSC, scheme);// 统计报表
							if (flag) {
								monitoringInfoLogic.saveMonitoringInfo(new MonitoringInfo(0, "CQSSC", shopCode, periodsInfo.getPeriodsNum(), "报表计算", 1, "商铺：" + shopCode + "重庆盘期"
								        + periodsInfo.getPeriodsNum() + "报表计算", (int) (System.currentTimeMillis() - startReport)));
							} else {
								monitoringInfoLogic.saveMonitoringInfo(new MonitoringInfo(0, "CQSSC", shopCode, periodsInfo.getPeriodsNum(), "报表计算", 0, "商铺：" + shopCode + "重庆盘期"
								        + periodsInfo.getPeriodsNum() + "报表计算", (int) (System.currentTimeMillis() - startReport)));
							}
						} catch (Exception ex) {
							monitoringInfoLogic.saveMonitoringInfo(new MonitoringInfo(0, "CQSSC", shopScheme.getKey(), periodsInfo.getPeriodsNum(), "重新兑奖", 0, "商铺：" + shopScheme.getKey() + "的重庆"
							        + periodsInfo.getPeriodsNum() + "盘期重新兑奖", (int) (System.currentTimeMillis() - star)));
						}
					}

				} else {
					logger.info("重庆时时彩  没有遗漏没有兑奖的数据在投注表");
				}
			} catch (Exception ex) {
				monitoringInfoLogic.saveMonitoringInfo(new MonitoringInfo(0, "CQSSC", shopCode, "0000000000", "重新兑奖", 0, "重庆时时彩,商铺:" + shopCode + "重新兑奖异常",
				        (int) (System.currentTimeMillis() - startTime)));
			}
		}

	}
}
