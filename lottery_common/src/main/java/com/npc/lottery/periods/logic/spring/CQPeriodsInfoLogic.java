package com.npc.lottery.periods.logic.spring;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.npc.lottery.common.Constant;
import com.npc.lottery.common.service.ShopSchemeService;
import com.npc.lottery.member.logic.interf.ICheckLogic;
import com.npc.lottery.periods.dao.interf.ICQPeriodsInfoDao;
import com.npc.lottery.periods.entity.CQPeriodsInfo;
import com.npc.lottery.periods.logic.interf.ICQPeriodsInfoLogic;
import com.npc.lottery.util.DatetimeUtil;
import com.npc.lottery.util.Page;

public class CQPeriodsInfoLogic implements ICQPeriodsInfoLogic {

    private Logger logger = Logger.getLogger(CQPeriodsInfoLogic.class);
    private ICQPeriodsInfoDao icqPeriodsInfoDao;
    //private DataExtractor dataExtractor;
    private ICheckLogic checkLogic;
    private ShopSchemeService shopSchemeService;
    /**
     * 这是每隔10分钟开一次的时时彩
     */
    @Override
    public void saveCQPeriods() {
        try {
            Date date = new Date();
            SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat openFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Calendar dateCalender = Calendar.getInstance();
            dateCalender.setTime(date);
            dateCalender.add(Calendar.DATE,1);
            String ageDate = ageFormat.format(dateCalender.getTime()) + " 10:00:15";
            Date openDate = openFormat.parse(ageDate);
            Calendar sysCalender = Calendar.getInstance();
            Calendar sysCalenderStop = Calendar.getInstance(); // 封盘时间设置
            sysCalender.setTime(openDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); // 盘期时间格式
            String dateString = formatter.format(sysCalender.getTime());
            int periodsCount = 96; // 期数
            int endPerDate = -90; // 提前封盘的时间
            for (int i = 25; i <= periodsCount; i++) {
                CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
                StringBuffer periodsNum = new StringBuffer(); // 期数设置
                periodsNum.append(dateString).append("0").append(i);
                cqPeriodsInfo.setPeriodsNum(String.valueOf(periodsNum));
                sysCalender.add(Calendar.MINUTE, 10); // 在开盘的基础上加10分钟到开奖的时间点
                Date lotteryDate = (Date) sysCalender.getTime();
                if (i == 1) {
                    // logger.info("开盘时间："+openFormat.format(openDate));
                    logger.info("开盘时间：" + openFormat.format(openDate));
                    cqPeriodsInfo.setOpenQuotTime(openDate);
                } else {
                    sysCalender.add(Calendar.MINUTE, -10); // -10分钟
                                                           // 在下轮开奖的基础上减去10分钟就是当前开盘时间
                    Date resultDate = (Date) sysCalender.getTime();
                    logger.info("开盘时间：" + openFormat.format(resultDate));
                    // logger.info("开盘时间："+resultDate);
                    cqPeriodsInfo.setOpenQuotTime(resultDate);
                }
                cqPeriodsInfo.setLotteryTime(lotteryDate);
                sysCalenderStop.setTime(lotteryDate);
                sysCalenderStop.add(Calendar.SECOND, endPerDate); // 开奖时间减去120秒就是这一轮封盘时间
                Date stopDate = (Date) sysCalenderStop.getTime();
                System.out.println("盘期" + periodsNum + "-" + "封盘时间"
                        + openFormat.format(stopDate) + "-"
                        + openFormat.format(lotteryDate) + "每一期开奖的时间");
                cqPeriodsInfo.setStopQuotTime(stopDate);
                cqPeriodsInfo.setResult1(0);
                cqPeriodsInfo.setResult2(0);
                cqPeriodsInfo.setResult3(0);
                cqPeriodsInfo.setResult4(0);
                cqPeriodsInfo.setResult5(0);
                cqPeriodsInfo.setState(Constant.NOT_STATUS);
                cqPeriodsInfo.setCreateTime(new Date());
                sysCalender.setTime(lotteryDate);
                icqPeriodsInfoDao.save(cqPeriodsInfo);

            }
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
    }

    /*
     * 97到120盘期每5分钟开一次的
     * @
     */
    @Override
    public void saveCQAfterPeriods() {
        try {
            Date date = new Date();
            SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat openFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Calendar dateCalender = Calendar.getInstance();
            dateCalender.setTime(date);
            dateCalender.add(Calendar.DATE,1);
            String ageDate = ageFormat.format(dateCalender.getTime()) + " 22:00:15";
            Date openDate = openFormat.parse(ageDate);
            Calendar sysCalender = Calendar.getInstance();
            Calendar sysCalenderStop = Calendar.getInstance(); // 封盘时间设置
            sysCalender.setTime(openDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); // 盘期时间格式
            String dateString = formatter.format(sysCalender.getTime());
            int periodsCount = 120; // 期数
            int endPerDate = -60; // 提前封盘的时间
            for (int i = 97; i <= periodsCount; i++) {
                CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
                StringBuffer periodsNum = new StringBuffer(); // 期数设置
                if (i >= 100) {
                    periodsNum.append(dateString).append(i);
                } else {
                    periodsNum.append(dateString).append("0").append(i);
                }
                cqPeriodsInfo.setPeriodsNum(String.valueOf(periodsNum));
                sysCalender.add(Calendar.MINUTE, 5); // 在开盘的基础上加5分钟到开奖的时间点
                Date lotteryDate = (Date) sysCalender.getTime();
                if (i == 1) {
                    cqPeriodsInfo.setOpenQuotTime(openDate);
                } else {
                    sysCalender.add(Calendar.MINUTE, -5); // -5分钟
                                                          // 在下轮开奖的基础上减去5分钟就是当前开盘时间
                    Date resultDate = (Date) sysCalender.getTime();
                    cqPeriodsInfo.setOpenQuotTime(resultDate);
                }

                cqPeriodsInfo.setLotteryTime(lotteryDate);
                sysCalenderStop.setTime(lotteryDate);
                sysCalenderStop.add(Calendar.SECOND, endPerDate); // 开奖时间减去60秒就是这一轮封盘时间
                Date stopDate = (Date) sysCalenderStop.getTime();
                cqPeriodsInfo.setStopQuotTime(stopDate);
                cqPeriodsInfo.setResult1(0);
                cqPeriodsInfo.setResult2(0);
                cqPeriodsInfo.setResult3(0);
                cqPeriodsInfo.setResult4(0);
				cqPeriodsInfo.setResult5(0);
				cqPeriodsInfo.setState(Constant.NOT_STATUS);
				cqPeriodsInfo.setCreateTime(new Date());
                sysCalender.setTime(lotteryDate);
                icqPeriodsInfoDao.save(cqPeriodsInfo);
            }
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
    }

    public ICQPeriodsInfoDao getIcqPeriodsInfoDao() {
        return icqPeriodsInfoDao;
    }

    public void setIcqPeriodsInfoDao(ICQPeriodsInfoDao icqPeriodsInfoDao) {
        this.icqPeriodsInfoDao = icqPeriodsInfoDao;
    }

    @Override
    public CQPeriodsInfo queryByPeriods(Criterion... criterias) {
    	
    	CQPeriodsInfo cqp=null;
    	List<CQPeriodsInfo> periodsList=icqPeriodsInfoDao.find(criterias);
    	if(periodsList!=null&&periodsList.size()!=0)
    		cqp=periodsList.get(0);
    	return cqp;
    	
   
    }
    
    @Override
    public Page<CQPeriodsInfo> findPage(Page<CQPeriodsInfo> page,Criterion... criterions) {
        return icqPeriodsInfoDao.findPage(page, criterions);
    }
    
    public CQPeriodsInfo queryByPeriodsStatus(String status)
    {
    	CQPeriodsInfo cqp=null;
    	String hql="from CQPeriodsInfo where  state=? and stopQuotTime>=? and openQuotTime<=? order by openQuotTime desc ";
    	List<CQPeriodsInfo> periodsList=icqPeriodsInfoDao.find(hql, status,new Date(),new Date());
    	if(periodsList!=null&&periodsList.size()!=0)
    		cqp=periodsList.get(0);
    	return cqp;
    	
    	
    }
    
    public CQPeriodsInfo queryStopPeriods(String status)
    {
    	CQPeriodsInfo cqp=null;
    	String hql="from CQPeriodsInfo where  state=? and lotteryTime>=? and openQuotTime<=? order by openQuotTime desc ";
    	List<CQPeriodsInfo> periodsList=icqPeriodsInfoDao.find(hql, status,new Date(),new Date());
    	if(periodsList!=null&&periodsList.size()!=0)
    		cqp=periodsList.get(0);
    	return cqp;
    	
    	
    }

    /**
     * 改变GD状态值
     */
    @Override
    public void updateCommon(String commonDate) {
         CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
         
         cqPeriodsInfo = queryLastNeedOpenPeriods(Constant.NOT_STATUS);
         
         if(cqPeriodsInfo!=null)
         {
        	 logger.info("重庆开盘 ：盘期号码："+cqPeriodsInfo.getPeriodsNum());
           cqPeriodsInfo.setState(Constant.OPEN_STATUS);
            try {
                   icqPeriodsInfoDao.save(cqPeriodsInfo);
            } catch (Exception e) {
                logger.info("更新异常"+e);
            }
           
         }
         else
        	 logger.info("重庆开盘 ：未拿到 重庆开盘盘期号 不开盘");
    }
    
    /**
     * 封盘
     */
    @Override
    public void updateCQStatus(){
        try {
            CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
            cqPeriodsInfo = queryLastNeedStopPeriods(Constant.OPEN_STATUS);
           
            if(cqPeriodsInfo!=null)
            {
             logger.info("重庆封盘：盘期号码 ："+cqPeriodsInfo.getPeriodsNum());
             cqPeriodsInfo.setState(Constant.STOP_STATUS);
             icqPeriodsInfoDao.update(cqPeriodsInfo);
            }
            else
            	 logger.info("重庆封盘：找不到封盘的盘期");
           
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行" + this.getClass().getSimpleName()
                    + "中的方法updateCQStatus时出现错误 "
                    + e.getMessage());
        }
       
    }

	/**
	 * 封盘并迁移投注数据到校验表
	 */
	/*@Override
	public void updateCQStatusWithCheckMiragation() {
		try {
			CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
			cqPeriodsInfo = queryLastNeedStopPeriods(Constant.OPEN_STATUS);

			if (cqPeriodsInfo != null) {
				logger.info("重庆封盘：盘期号码 ：" + cqPeriodsInfo.getPeriodsNum());
				cqPeriodsInfo.setState(Constant.STOP_STATUS);
				icqPeriodsInfoDao.update(cqPeriodsInfo);
				// 获取商铺对应的scheme列表
				Map<String, String> shopSchemeMap = shopSchemeService.getShopSchemeMap();

				// 每一个商铺按每期进去兑奖
				for (Entry<String, String> shopScheme : shopSchemeMap.entrySet()) {
					String scheme = shopScheme.getValue();
					// 迁移投注数据
					checkLogic.miragationCQDataToCheckTable(cqPeriodsInfo.getPeriodsNum(), scheme);
					// 迁移重庆补货数据
				}
			} else {
				logger.info("重庆封盘：找不到封盘的盘期");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("执行" + this.getClass().getSimpleName() + "中的方法updateCQStatus时出现错误 " + e.getMessage());
		}

	}*/
    
    private CQPeriodsInfo queryLastNeedStopPeriods(String status)
    {
        CQPeriodsInfo cqp=null;
        String hql = "from CQPeriodsInfo where  state=?  and lotteryTime >?   order by openQuotTime asc";       
        Object[] parameter = new Object[] { status,new Date()};
        List<CQPeriodsInfo> cqpList=icqPeriodsInfoDao.find(hql, parameter);
        if(cqpList!=null&&cqpList.size()>0)
            cqp=cqpList.get(0);
         return cqp;
    }
    
    @Override
    public CQPeriodsInfo queryByPeriods(String periodsNum, String number) {
        return icqPeriodsInfoDao.findUniqueBy(periodsNum, number);
    }

    
    @Override
    public void save(CQPeriodsInfo caPeriodsInfo) {
        icqPeriodsInfoDao.save(caPeriodsInfo);
    }

    @Override
    public List<CQPeriodsInfo> queryAllPeriods(Criterion... criterias) {
        return icqPeriodsInfoDao.find(criterias);
    }

   /**
    * 这是1到24期每5分钟开一次的盘期
    */
    @Override
    public void saveCQInterimPeriods() {
        try {
            Date date = new Date();
            SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat openFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Calendar dateCalender = Calendar.getInstance();
            dateCalender.setTime(date);
            dateCalender.add(Calendar.DATE,1);
            String ageDate = ageFormat.format(dateCalender.getTime()) + " 00:00:15";
            Date openDate = openFormat.parse(ageDate);
            logger.info(openDate + "---openDate---");
            Calendar sysCalender = Calendar.getInstance();
            Calendar sysCalenderStop = Calendar.getInstance(); // 封盘时间设置
            sysCalender.setTime(openDate);
            logger.info(sysCalender.getTime() + "--------------------");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); // 盘期时间格式
            String dateString = formatter.format(sysCalender.getTime());
            int periodsCount = 24; // 期数
            int endPerDate = -60; // 提前封盘的时间
            int count = 1;
            for (int i = 1; i <= periodsCount; i++) {
                CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
                StringBuffer periodsNum = new StringBuffer(); // 期数设置
                if (i >= 10) {
                    periodsNum.append(dateString).append("0").append(i);
                } else {
                    periodsNum.append(dateString).append("00").append(i);
                }
                cqPeriodsInfo.setPeriodsNum(String.valueOf(periodsNum));
                sysCalender.add(Calendar.MINUTE, 5); // 在开盘的基础上加5分钟到开奖的时间点
                Date lotteryDate = (Date) sysCalender.getTime();
                if (i == 1) {
                    logger.info(openDate + "---openDate11111---");
                    cqPeriodsInfo.setOpenQuotTime(openDate);
                    System.out.println("开盘时间：" + openFormat.format(openDate));
                } else {
                    sysCalender.add(Calendar.MINUTE, -5); // -5分钟
                                                          // 在下轮开奖的基础上减去5分钟就是当前开盘时间
                    Date resultDate = (Date) sysCalender.getTime();
                    logger.info("开盘时间：" + openFormat.format(resultDate));
                    cqPeriodsInfo.setOpenQuotTime(resultDate);
                }
                cqPeriodsInfo.setLotteryTime(lotteryDate);
                sysCalenderStop.setTime(lotteryDate);
                sysCalenderStop.add(Calendar.SECOND, endPerDate); // 开奖时间减去60秒就是这一轮封盘时间
                Date stopDate = (Date) sysCalenderStop.getTime();
                //第24期不给投注
                if(StringUtils.endsWithIgnoreCase(periodsNum.toString(), "024"))
                {
                	//stopDate=openDate;
                	cqPeriodsInfo.setState(Constant.STOP_STATUS);
                }else {
                	cqPeriodsInfo.setState(Constant.NOT_STATUS);
                }
                logger.info("盘期" + periodsNum + "-" + "封盘时间"
                        + openFormat.format(stopDate) + "-"
                        + openFormat.format(lotteryDate) + "每一期开奖的时间");
                cqPeriodsInfo.setStopQuotTime(stopDate);
                cqPeriodsInfo.setResult1(0);
                cqPeriodsInfo.setResult2(0);
                cqPeriodsInfo.setResult3(0);
                cqPeriodsInfo.setResult4(0);
                cqPeriodsInfo.setResult5(0);
                //cqPeriodsInfo.setState(Constant.NOT_STATUS);
                cqPeriodsInfo.setCreateTime(new Date());
                sysCalender.setTime(lotteryDate);
                icqPeriodsInfoDao.save(cqPeriodsInfo);
                count++;
            }
            logger.info(count + "一共是多少");

        } catch (ParseException e2) {
            e2.printStackTrace();
        }

    }

    @Override
    public List<CQPeriodsInfo> queryTodayPeriods() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // sdf.format();
        java.util.Date now = new java.util.Date();
        String startDateStr=DatetimeUtil.addTime(null, "H", -2);
        Date startDate= null;
        try {
        	startDate=sdf.parse(startDateStr);
		} catch (ParseException e) {		
			e.printStackTrace();
		}
        String startStr = sdf.format(startDate);
        String nowStr = sdf.format(now);
        String before = startStr + " 00:00:00";
        String after = nowStr + " 23:59:59";
        Date bef = java.sql.Timestamp.valueOf(before);
        Date af = java.sql.Timestamp.valueOf(after);

        String hql = "from CQPeriodsInfo where lotteryTime >=? and lotteryTime<=? and state in (4,7) order by lotteryTime desc";
        Object[] parameter = new Object[] { bef, af };
        return icqPeriodsInfoDao.find(hql, parameter);
    }
    
    @Override
    public List<CQPeriodsInfo> queryTodayAllPeriods() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // sdf.format();
        java.util.Date now = new java.util.Date();
        String nowStr = sdf.format(now);
        String before = nowStr + " 00:00:00";
        String after = nowStr + " 23:59:59";
        Date bef = java.sql.Timestamp.valueOf(before);
        Date af = java.sql.Timestamp.valueOf(after);

        String hql = "from CQPeriodsInfo where  lotteryTime>=? and lotteryTime<=? order by lotteryTime desc";
        Object[] parameter = new Object[] { bef, af };
        return icqPeriodsInfoDao.find(hql, parameter);
    }
    
    @Override
    public List<CQPeriodsInfo> queryLastPeriodsForRefer() {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        
        String nowStr = sdf.format(now);
        String yes=DatetimeUtil.yesterday();
        String after = nowStr + " 23:59:59";
        String yesStr=yes+" 22:59:59";
        Date af = java.sql.Timestamp.valueOf(after);
        Date yestoday=java.sql.Timestamp.valueOf(yesStr);
        String hql = "from CQPeriodsInfo where lotteryTime>=? and lotteryTime<=? and state in (4,7) order by lotteryTime desc";
        Object[] parameter = new Object[] {yestoday,  af };
        
        Page<CQPeriodsInfo> page=new Page<CQPeriodsInfo>();
        page.setPageSize(100);
        page.setPageNo(1);
        Page<CQPeriodsInfo> pageResult=icqPeriodsInfoDao.findPage(page, hql, parameter);
        List<CQPeriodsInfo> periodsList=pageResult.getResult();

        
        
       // List<CQPeriodsInfo> periodsList=icqPeriodsInfoDao.find(hql, parameter);
       // if(periodsList.size()>30)
        //	periodsList=periodsList.subList(0, 30);	
        return  periodsList;

    }
    
    
    
    
    

    public Page<CQPeriodsInfo> queryHistoryPeriods(Page<CQPeriodsInfo> page) {
        String hql = "from CQPeriodsInfo where  state in (4,6,7) order by lotteryTime desc";
        Object[] parameter = new Object[] {};
        return icqPeriodsInfoDao.findPage(page, hql, parameter);
    }
    
    //
    public Page<CQPeriodsInfo> queryHistoryPeriodsForBoss(Page<CQPeriodsInfo> page) {
    	//String hql = "from CQPeriodsInfo where  state in (4,5,6,7) order by lotteryTime desc";
    	String hql = "from CQPeriodsInfo  where  state not in (1,2) order by lotteryTime desc";
    	Object[] parameter = new Object[] {};
    	return icqPeriodsInfoDao.findPage(page, hql, parameter);
    }

	public static void main(String[] args) {

		// System.out.println(DatetimeUtil.addTime(null, "H", -2));
		// System.out.println(StringUtils.endsWithIgnoreCase("20121021025", "024"));
		/*
		 * new CQPeriodsInfoLogic().saveCQPeriods(); new CQPeriodsInfoLogic().saveCQAfterPeriods();
		 */

		ApplicationContext context = new FileSystemXmlApplicationContext("F:/JAVA/workspace/Lottery20121004/WEB-INF/applicationContext.xml");
		ICQPeriodsInfoLogic test = (ICQPeriodsInfoLogic) context.getBean("icqPeriodsInfoLogic");
		// System.out.println(test.findAllExceptionPeriodInfo().size());
		// System.out.println(test.getResult());
		// System.out.println(test.getResultByPeriodsNum("20130414075"));
		// System.out.println(test.getResultByPeriodsNum("111"));
		//System.out.println(test.queryTodayPeriodsForEOD());
		//System.out.println(test.getPeriodsInfoFromBetTableForReLotteryEOD().size());
		System.out.println(test.findAllExceptionPeriodInfoByDate(new Date()).size());
	}

    // 保存修改重庆时时彩的开奖结果
    @Override
    public void updateLotResult(CQPeriodsInfo cqPeriodsInfo) {
        CQPeriodsInfo cqp = new CQPeriodsInfo();
        cqp = icqPeriodsInfoDao.findUniqueBy("periodsNum",
                cqPeriodsInfo.getPeriodsNum());
        cqp.setResult1(cqPeriodsInfo.getResult1());
        cqp.setResult2(cqPeriodsInfo.getResult2());
        cqp.setResult3(cqPeriodsInfo.getResult3());
        cqp.setResult4(cqPeriodsInfo.getResult4());
        cqp.setResult5(cqPeriodsInfo.getResult5());
        cqp.setState(cqPeriodsInfo.getState());
        
        icqPeriodsInfoDao.save(cqp);
        // 这里增加其他操作

    }
    public CQPeriodsInfo queryLastLotteryPeriods()
    {
    	CQPeriodsInfo cqp=null;
    	String yes=DatetimeUtil.yesterday();	       
        String yesStr=yes+" 22:59:59"; 
        Date yestoday=java.sql.Timestamp.valueOf(yesStr);
    	String hql = "from CQPeriodsInfo where  state in (4,7) and lotteryTime>=? order by lotteryTime desc";   	
    	Object[] parameter = new Object[] {yestoday};
    	List<CQPeriodsInfo> cqpList=icqPeriodsInfoDao.find(hql, parameter);
    	if(cqpList!=null&&cqpList.size()>0)
    	{	cqp=cqpList.get(0);
    	    cqpList.clear();
    	}
         return cqp;
    }
    
    public CQPeriodsInfo queryLastNotOpenPeriods()
    {
    	CQPeriodsInfo cqp=null;
    	String hql = "from CQPeriodsInfo where  state=1 and openQuotTime>? order by openQuotTime asc";  	
    	Object[] parameter = new Object[] {new Date() };
    	List<CQPeriodsInfo> cqpList=icqPeriodsInfoDao.find(hql, parameter);
    	if(cqpList!=null&&cqpList.size()>0)
    		cqp=cqpList.get(0);
         return cqp;
    }

    @Override
    public List<CQPeriodsInfo> queryAllPeriods(String propertyName, String value) {
        return icqPeriodsInfoDao.findBy(propertyName, value);
    }

    @Override
    public void update(CQPeriodsInfo cqPeriodsInfo) {
        icqPeriodsInfoDao.update(cqPeriodsInfo);
    }
   
    private CQPeriodsInfo queryLastNeedOpenPeriods(String status)
    {
    	CQPeriodsInfo cqp=null;

		
		Date now2 =DateUtils.addSeconds(new Date(), 20);
    	String hql = "from CQPeriodsInfo where  state=?  and stopQuotTime>? and openQuotTime<=?  order by openQuotTime asc";   	
    	Object[] parameter = new Object[] { status,new Date(),now2};
    	List<CQPeriodsInfo> cqpList=icqPeriodsInfoDao.find(hql, parameter);
    	if(cqpList!=null&&cqpList.size()>0)
    		cqp=cqpList.get(0);
         return cqp;
    }
    @Override
    public void updatePeriodsStatusByPeriodsNum(String periodsNum,String status){
	 
	 String hql="update CQPeriodsInfo set state=? where periodsNum=? ";
	 icqPeriodsInfoDao.batchExecute(hql, status,periodsNum);
	 
	
    }

    @Override
    public void deletePeriods(String beginPeriodsNum,String endPeriodsNum) {
        String hql = "delete CQPeriodsInfo where periodsNum>=? and periodsNum <=?";
        icqPeriodsInfoDao.batchExecute(hql, beginPeriodsNum,endPeriodsNum);
    }

    @Override
    public List<CQPeriodsInfo> queryReportPeriodsNum() {
    	String hql = "from CQPeriodsInfo where  state=7  and lotteryTime <=?   order by lotteryTime desc";       
    	Object[] parameter = new Object[] {new Date()};
    	Page<CQPeriodsInfo> page = new Page<CQPeriodsInfo>();
    	page.setPageSize(50);
    	page.setPageNo(1);
    	page = icqPeriodsInfoDao.findPage(page, hql, parameter);
    	return  page.getResult();
    }
    @Override
    public List<CQPeriodsInfo> queryBeforeRunPeriodsNumList(Integer row) {
    	Calendar afterTime = Calendar.getInstance();
  	  afterTime.add(Calendar.MINUTE, 10);
  	  Date afterDate = (Date) afterTime.getTime(); 
  	  
  	  
        String hql = "from CQPeriodsInfo where lotteryTime <=? order by lotteryTime desc";       
        Object[] parameter = new Object[] {afterDate};
        Page<CQPeriodsInfo> page = new Page<CQPeriodsInfo>();
        page.setPageSize(row);
        page.setPageNo(1);
        page = icqPeriodsInfoDao.findPage(page, hql, parameter);
        return  page.getResult();
    }

    @Override
    public List<CQPeriodsInfo> queryPeriods(String beginPeriodsNum, String endPeriodsNum) {
        String hql = "from CQPeriodsInfo where periodsNum>=? and periodsNum <=?";
        Object[] parameter = new Object[] {beginPeriodsNum,endPeriodsNum};
        return icqPeriodsInfoDao.find(hql, parameter);
    }

    @Override
    public void saveManualCQInterimPeriods() {
        try {
            Date date = new Date();
            SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat openFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Calendar dateCalender = Calendar.getInstance();
            dateCalender.setTime(date);
            String ageDate = ageFormat.format(dateCalender.getTime()) + " 00:00:15";
            Date openDate = openFormat.parse(ageDate);
            logger.info(openDate + "---openDate---");
            Calendar sysCalender = Calendar.getInstance();
            Calendar sysCalenderStop = Calendar.getInstance(); // 封盘时间设置
            sysCalender.setTime(openDate);
            logger.info(sysCalender.getTime() + "--------------------");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); // 盘期时间格式
            String dateString = formatter.format(sysCalender.getTime());
            int periodsCount = 24; // 期数
            int endPerDate = -60; // 提前封盘的时间
            int count = 1;
            for (int i = 1; i <= periodsCount; i++) {
                CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
                StringBuffer periodsNum = new StringBuffer(); // 期数设置
                if (i >= 10) {
                    periodsNum.append(dateString).append("0").append(i);
                } else {
                    periodsNum.append(dateString).append("00").append(i);
                }
                cqPeriodsInfo.setPeriodsNum(String.valueOf(periodsNum));
                sysCalender.add(Calendar.MINUTE, 5); // 在开盘的基础上加5分钟到开奖的时间点
                Date lotteryDate = (Date) sysCalender.getTime();
                if (i == 1) {
                    logger.info(openDate + "---openDate11111---");
                    cqPeriodsInfo.setOpenQuotTime(openDate);
                    System.out.println("开盘时间：" + openFormat.format(openDate));
                } else {
                    sysCalender.add(Calendar.MINUTE, -5); // -5分钟
                                                          // 在下轮开奖的基础上减去5分钟就是当前开盘时间
                    Date resultDate = (Date) sysCalender.getTime();
                    logger.info("开盘时间：" + openFormat.format(resultDate));
                    cqPeriodsInfo.setOpenQuotTime(resultDate);
                }
                cqPeriodsInfo.setLotteryTime(lotteryDate);
                sysCalenderStop.setTime(lotteryDate);
                sysCalenderStop.add(Calendar.SECOND, endPerDate); // 开奖时间减去60秒就是这一轮封盘时间
                Date stopDate = (Date) sysCalenderStop.getTime();
                if(StringUtils.endsWithIgnoreCase(periodsNum.toString(), "024"))
                {
                	//stopDate=openDate;
                	cqPeriodsInfo.setState(Constant.STOP_STATUS);
                }else{
					cqPeriodsInfo.setState(Constant.NOT_STATUS);
                }
                logger.info("盘期" + periodsNum + "-" + "封盘时间"
                        + openFormat.format(stopDate) + "-"
                        + openFormat.format(lotteryDate) + "每一期开奖的时间");
                cqPeriodsInfo.setStopQuotTime(stopDate);
                cqPeriodsInfo.setResult1(0);
                cqPeriodsInfo.setResult2(0);
                cqPeriodsInfo.setResult3(0);
                cqPeriodsInfo.setResult4(0);
                cqPeriodsInfo.setResult5(0);
                //cqPeriodsInfo.setState(Constant.NOT_STATUS);
                cqPeriodsInfo.setCreateTime(new Date());
                sysCalender.setTime(lotteryDate);
                icqPeriodsInfoDao.save(cqPeriodsInfo);
                count++;
            }
            logger.info(count + "一共是多少");

        } catch (ParseException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void saveManualCQPeriods() {
        try {
            Date date = new Date();
            SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat openFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Calendar dateCalender = Calendar.getInstance();
            dateCalender.setTime(date);
            String ageDate = ageFormat.format(dateCalender.getTime()) + " 10:00:15";
            Date openDate = openFormat.parse(ageDate);
            Calendar sysCalender = Calendar.getInstance();
            Calendar sysCalenderStop = Calendar.getInstance(); // 封盘时间设置
            sysCalender.setTime(openDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); // 盘期时间格式
            String dateString = formatter.format(sysCalender.getTime());
            int periodsCount = 96; // 期数
            int endPerDate = -90; // 提前封盘的时间
            for (int i = 25; i <= periodsCount; i++) {
                CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
                StringBuffer periodsNum = new StringBuffer(); // 期数设置
                periodsNum.append(dateString).append("0").append(i);
                cqPeriodsInfo.setPeriodsNum(String.valueOf(periodsNum));
                sysCalender.add(Calendar.MINUTE, 10); // 在开盘的基础上加10分钟到开奖的时间点
                Date lotteryDate = (Date) sysCalender.getTime();
                if (i == 1) {
                    // logger.info("开盘时间："+openFormat.format(openDate));
                    logger.info("开盘时间：" + openFormat.format(openDate));
                    cqPeriodsInfo.setOpenQuotTime(openDate);
                } else {
                    sysCalender.add(Calendar.MINUTE, -10); // -10分钟
                                                           // 在下轮开奖的基础上减去10分钟就是当前开盘时间
                    Date resultDate = (Date) sysCalender.getTime();
                    logger.info("开盘时间：" + openFormat.format(resultDate));
                    // logger.info("开盘时间："+resultDate);
                    cqPeriodsInfo.setOpenQuotTime(resultDate);
                }
                cqPeriodsInfo.setLotteryTime(lotteryDate);
                sysCalenderStop.setTime(lotteryDate);
                sysCalenderStop.add(Calendar.SECOND, endPerDate); // 开奖时间减去120秒就是这一轮封盘时间
                Date stopDate = (Date) sysCalenderStop.getTime();
                System.out.println("盘期" + periodsNum + "-" + "封盘时间"
                        + openFormat.format(stopDate) + "-"
                        + openFormat.format(lotteryDate) + "每一期开奖的时间");
                cqPeriodsInfo.setStopQuotTime(stopDate);
                cqPeriodsInfo.setResult1(0);
                cqPeriodsInfo.setResult2(0);
                cqPeriodsInfo.setResult3(0);
                cqPeriodsInfo.setResult4(0);
                cqPeriodsInfo.setResult5(0);
                cqPeriodsInfo.setState(Constant.NOT_STATUS);
                cqPeriodsInfo.setCreateTime(new Date());
                sysCalender.setTime(lotteryDate);
                icqPeriodsInfoDao.save(cqPeriodsInfo);

            }
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void saveManualCQAfterPeriods() {
        try {
            Date date = new Date();
            SimpleDateFormat ageFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat openFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Calendar dateCalender = Calendar.getInstance();
            dateCalender.setTime(date);
            String ageDate = ageFormat.format(dateCalender.getTime()) + " 22:00:15";
            Date openDate = openFormat.parse(ageDate);
            Calendar sysCalender = Calendar.getInstance();
            Calendar sysCalenderStop = Calendar.getInstance(); // 封盘时间设置
            sysCalender.setTime(openDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); // 盘期时间格式
            String dateString = formatter.format(sysCalender.getTime());
            int periodsCount = 120; // 期数
            int endPerDate = -60; // 提前封盘的时间
            for (int i = 97; i <= periodsCount; i++) {
                CQPeriodsInfo cqPeriodsInfo = new CQPeriodsInfo();
                StringBuffer periodsNum = new StringBuffer(); // 期数设置
                if (i >= 100) {
                    periodsNum.append(dateString).append(i);
                } else {
                    periodsNum.append(dateString).append("0").append(i);
                }
                cqPeriodsInfo.setPeriodsNum(String.valueOf(periodsNum));
                sysCalender.add(Calendar.MINUTE, 5); // 在开盘的基础上加5分钟到开奖的时间点
                Date lotteryDate = (Date) sysCalender.getTime();
                if (i == 1) {
                    cqPeriodsInfo.setOpenQuotTime(openDate);
                } else {
                    sysCalender.add(Calendar.MINUTE, -5); // -5分钟
                                                          // 在下轮开奖的基础上减去5分钟就是当前开盘时间
                    Date resultDate = (Date) sysCalender.getTime();
                    cqPeriodsInfo.setOpenQuotTime(resultDate);
                }

                cqPeriodsInfo.setLotteryTime(lotteryDate);
                sysCalenderStop.setTime(lotteryDate);
                sysCalenderStop.add(Calendar.SECOND, endPerDate); // 开奖时间减去60秒就是这一轮封盘时间
                Date stopDate = (Date) sysCalenderStop.getTime();
                cqPeriodsInfo.setStopQuotTime(stopDate);
                cqPeriodsInfo.setResult1(0);
                cqPeriodsInfo.setResult2(0);
                cqPeriodsInfo.setResult3(0);
                cqPeriodsInfo.setResult4(0);
                cqPeriodsInfo.setResult5(0);
                cqPeriodsInfo.setState(Constant.NOT_STATUS);
                cqPeriodsInfo.setCreateTime(new Date());
                sysCalender.setTime(lotteryDate);
                icqPeriodsInfoDao.save(cqPeriodsInfo);
            }

        } catch (ParseException e2) {
            e2.printStackTrace();
        }
    }
    
    @Override
    public CQPeriodsInfo queryZhangdanPeriods()
    {
    	CQPeriodsInfo cqPeriodsInfo = null;
    	String hql="FROM CQPeriodsInfo WHERE lotteryTime>=? AND openQuotTime<=? AND state not in (4,7)";
    	List<CQPeriodsInfo> periodsList=icqPeriodsInfoDao.find(hql, new Date(),new Date());
    	if(periodsList!=null&&periodsList.size()!=0)
    		cqPeriodsInfo = periodsList.get(0);
    	return cqPeriodsInfo;
    	
    }

	/*@Override
	public Map<String, List<Integer>> getResult() {
		return dataExtractor.getResult();
	}

	@Override
	public Map<String, List<Integer>> getResultByPeriodsNum(String periodsNum) {
		return dataExtractor.getResultByPeriodsNum(periodsNum);
	}

	public DataExtractor getDataExtractor() {
		return dataExtractor;
	}

	public void setDataExtractor(DataExtractor dataExtractor) {
		this.dataExtractor = dataExtractor;
	}*/

	@Override
	public CQPeriodsInfo findCurrentStopPeriodInfo() {
		final String hql = "FROM CQPeriodsInfo WHERE state=3 AND stopQuotTime>= to_date(:todayStr,'yyyyMMdd') AND lotteryTime<= :now ORDER BY periodsNum DESC";
		Date now = new Date();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("now", DateUtils.addSeconds(now, 20));
		paramMap.put("todayStr", new SimpleDateFormat("yyyyMMdd").format(now));
		List<CQPeriodsInfo> results = icqPeriodsInfoDao.find(hql, paramMap);
		return !CollectionUtils.isEmpty(results) ? results.get(0) : null;
	}

	@Override
	public List<CQPeriodsInfo> findAllExceptionPeriodInfo() {
		// 获取状态是5的或者比当前盘期期数小,但状态是1/2/3的数据
		/*final String hql = "FROM CQPeriodsInfo WHERE "
				+ "(state=5 OR (periodsNum < (SELECT MAX(periodsNum)-1 FROM  CQPeriodsInfo WHERE :today > openQuotTime  AND :today <=lotteryTime) AND state IN (1,2,3))) "
				+ "AND lotteryTime >= to_date(:checkDate,'yyyyMMdd') ORDER BY periodsNum desc";*/
		
		final String hql="FROM CQPeriodsInfo WHERE state not in (6,7) and lottery_time <= :sysdate ORDER BY periods_num desc";
		Date today = new Date();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sysdate", today);
		//paramMap.put("today", today);
		//paramMap.put("checkDate", String.valueOf(Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(today)) - 1));
		//paramMap.put("checkDate", new SimpleDateFormat("yyyyMMdd").format(today));
		List<CQPeriodsInfo> result = icqPeriodsInfoDao.find(hql, paramMap);
		//return result.subList(0, result.size() > 10 ? 10 : result.size());
		//return result.subList(0, result.size() > 1 ? 1 : result.size());
		return result;
	}
	
	@Override
	public List<CQPeriodsInfo> queryTodayPeriodsForEOD() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// sdf.format();
		java.util.Date now = new java.util.Date();

		String startStr = sdf.format(DateUtils.addDays(now, -1));
		String nowStr = sdf.format(now);
		String before = startStr + " 02:00:00";
		String after = nowStr + " 02:00:00";
		Date startTime = java.sql.Timestamp.valueOf(before);
		Date endTime = java.sql.Timestamp.valueOf(after);

		String hql = "FROM CQPeriodsInfo where lotteryTime > :startTime and lotteryTime<= :endTime and state in (4,7) order by lotteryTime desc";
		Map<String,Date> paramMap = new HashMap<String, Date>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		return icqPeriodsInfoDao.find(hql, paramMap);
	}
	
	@Override
	public Map<String, CQPeriodsInfo> getPeriodsInfoFromBetTableForReLotteryEOD(String scheme) {
		Map<String, CQPeriodsInfo> resultMap = new TreeMap<String, CQPeriodsInfo>();
		List<CQPeriodsInfo> resultList = icqPeriodsInfoDao.getPeriodsInfoFromBetTableForReLotteryEOD(scheme);
		if (!CollectionUtils.isEmpty(resultList)) {
			for (CQPeriodsInfo periodsInfo : resultList) {
				resultMap.put(periodsInfo.getPeriodsNum(), periodsInfo);
			}
		}
		return resultMap;
	}
	
	@Override
	public List<CQPeriodsInfo> findAllExceptionPeriodInfoByDate(Date queryDate) {
		List<CQPeriodsInfo> result = null;
		try {
			// 获取状态是5的queryDate盘期
			final String hql = "FROM CQPeriodsInfo WHERE state not in (4,6,7) AND openQuotTime BETWEEN :queryDateFrom AND :queryDateTo order by periodsNum desc";

			Map<String, Object> paramMap = new HashMap<String, Object>();
			SimpleDateFormat shortFormat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat longFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

			paramMap.put("queryDateFrom", longFormat.parse(shortFormat.format(queryDate) + " 10:00:00"));
			paramMap.put("queryDateTo", longFormat.parse(shortFormat.format(DateUtils.addDays(queryDate, 1)) + " 02:00:00"));
			result = icqPeriodsInfoDao.find(hql, paramMap);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public CQPeriodsInfo findCurrentPeriod() {
		final String hql = "FROM CQPeriodsInfo WHERE openQuotTime <= :sysdate and lotteryTime >= :sysdate";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sysdate", new Date());
		List<CQPeriodsInfo> result = icqPeriodsInfoDao.find(hql, paramMap);
		return result.size() > 0 ? result.get(0) : null;
	}
	
	public ICheckLogic getCheckLogic() {
		return checkLogic;
	}

	public void setCheckLogic(ICheckLogic checkLogic) {
		this.checkLogic = checkLogic;
	}

	public ShopSchemeService getShopSchemeService() {
		return shopSchemeService;
	}

	public void setShopSchemeService(ShopSchemeService shopSchemeService) {
		this.shopSchemeService = shopSchemeService;
	}
}

