package net.leelink.healthangelos.util;

public class Urls {
    public static String IP = "";
    public static String H5_IP = "";
    public static String C_IP = "";

    public String WEBSITE = IP + "/jk/healthAngel/";
    public String C_WEBSITE = IP + "/sh/customer/";
    public String U_WEBSITE = IP + "/sh/user/";


    public static Urls getInstance() {

        return new Urls();

    }

    //获取商户编码
    public static String PARTNER_CODE = "http://api.llky.net:8888/partner/user/";

    //获取版本信息
    public static String VERSION = "http://api.llky.net:8888/app/version";   //获取版本更新


    //上传用户头像
    public String UPLOADHEADIMAGE = IP+"/sh/customer/UploadHeadImage";


    //社区活动地址
    public String COMMUNITY_WEB = H5_IP + "/#/ActiveTemp/community/";

    //个人任务详情
    public String SINGLE_MISSION = H5_IP + "/#/TaskTemp/perTaskCon/";

    //团队任务详情

    public String TEAM_MISSION = H5_IP + "/#/TaskTemp/TeamTaskCon/";

    //调查问卷地址
    public String ESTIMATE = H5_IP + "/#/QuestTemp/wholeQuest/";


    //h5健康数据地址
    public String WEB = H5_IP + "/#/AppData";

    public String IMG_URL = IP + "/files";

    //发送短信验证码
    public String SEND = U_WEBSITE + "send";

    //注册
    public String REGISTER = C_WEBSITE + "regist";

    //密码登录
    public String LOGIN = WEBSITE + "login";

    //修改密码
    public String CHANGEPASSWORD = C_WEBSITE + "changePassword";

    //验证码登录
    public String LOGINBYCODE = WEBSITE + "loginByCode";

    //首页轮播图
    public String HOMEBANNER = WEBSITE + "homeBanner";

    //每日监测排名
    public String RANK = WEBSITE + "rank";

    //养老资讯
    public String NEWS = WEBSITE + "news";

    //极速诊询医生
    public String DOCTOR = WEBSITE + "docter";

    //新增电子围栏范围
    public String ELECTRADDRESS = WEBSITE + "electrAddress";

    //电子围栏计划
    public String ELECTRPLAN = WEBSITE + "electrPlan";

    //查询个人信息
    public String USERINFO = WEBSITE + "userInfo";

    //一键定位
    public String OPENGPS = WEBSITE + "openGPS";

    //查询定位历史
    public String GPSRECORD = WEBSITE + "gpsRecord";

    //获取定时提醒列表
    public String REMINDLIST = WEBSITE + "remindList";

    //新增定时提醒
    public String ADDREMIND = WEBSITE + "addRemind";

    //修改定时提醒
    public String UPDATEREMIND = WEBSITE + "updateRemind";

    //删除定时提醒
    public String DELETEREMIND = WEBSITE + "deleteRemind";

    //数据录入
    public String INPUT = WEBSITE + "input";

    //录入的历史记录
    public String INPUTLIST = WEBSITE + "inputList";

    //获取公共模板
    public String VOICETEMPLATE = WEBSITE + "voiceTemplate";

    //语音播报内容
    public String VOICECONTENT = WEBSITE + "voiceContent";

    //语音播报历史记录
    public String FINDMSG = WEBSITE + "findMsg";

    //发送语音播报
    public String SENDMESSAGE = WEBSITE + "sendMessage";

    //定时发送
    public String SENDMESSAGEBYTIME = WEBSITE + "sendMessageByTime";

    //查询绑定的设备
    public String MYBIND = WEBSITE + "mybind";

    //修改腕表别名
    public String NICKNAME = WEBSITE +"nickname";

    //查询绑定的设备详情
    public String BIND_DETAILS = WEBSITE +"bind-details";

    //修改绑定腕表号码
    public String BINDPHONE = WEBSITE +"bindphone";

    //修改腕表跑步目标
    public String RUN_TARGET = WEBSITE +"run-target";

    //修改睡眠目标
    public String SLEEP_TARGET = WEBSITE +"sleep-target";

    //修改入睡时间
    public String SLEEP_LATENCY = WEBSITE +"sleep-latency";

    //绑定\解除设备 查询可绑定设备
    public String BIND = WEBSITE + "bind";


    //我的信息
    public String INFO = WEBSITE + "info";

    //查询健康数据
    public String HEALTHDATA = WEBSITE + "heatlhData";

    //常见食物列表
    public String FOODRECORD = WEBSITE + "foodRecord";

    //新增/查询饮食记录
    public String RECORD = WEBSITE + "record";

    //查询最近添加的食物
    public String RECENTRECORD = WEBSITE + "recentRecord";

    //查询附近机构
    public String NEARORGAN = WEBSITE + "nearOrgan";

    //查询城市列表
    public String GETALLCITY = IP + "/sysDict/getAllCity";

    //设置亲人号码
    public String RELATIVE = WEBSITE + "relative";

    //获取亲人号码列表
    public String RELATIVECONTACTLIST = WEBSITE + "relativeContactList";

    //新增紧急联系人
    public String URGENTPEOPLE = WEBSITE + "urgentPeople";

    //编辑紧急联系人
    public String UPDATEURGENTPEOPLE = WEBSITE + "updateUrgentPeople";

    //惠民政策列表
    public String BENEFIT = WEBSITE + "benefit";

    //健康知识列表
    public String KNOWLEDGE = WEBSITE + "knowledge";

    //查询调查问卷模板
    public String QUESTIONTEMP = WEBSITE + "questionTemp";

    //申请高龄补贴
    public String EVALUTION = WEBSITE + "evalution";

    //获取组织列表
    public String ORGAN = IP + "/sh/user/organ";

    //获取街道
    public String GETTOWN = IP + "/sysDict/getTown";

    //设备维修
    public String EQUIP = WEBSITE + "equip";

    //IC卡充值
    public String ICPAY = WEBSITE + "icPay";

    //充值账单
    public String ACCOUNT = WEBSITE + "account";

    //查询所有可订阅的服务
    public String SERVICE = WEBSITE + "service";

    //查询三诺血糖仪测量血糖记录
    public String SANNUOBLOODSUGARLIST = WEBSITE + "sannuoBloodSugarList";

    //查询三诺血尿酸记录
    public String SANNUOBLOODURICLIST = WEBSITE + "sannuoBloodUricList";

    //上传血糖数据
    public String UPLOADBLOODSUGAR = WEBSITE + "uploadBloodSugar";

    //上传血尿酸数据
    public String UPLOADUA = WEBSITE + "uploadUa";

    //查询套餐列表
    public String MEAL = WEBSITE + "meal";

    //我的套餐
    public String MEAL_MINE = WEBSITE + "meal-mine";

    //社区活动列表
    public String ACTION = WEBSITE + "action";

    //我的社区活动
    public String ACTION_MINE = WEBSITE + "action-mine";

    //实名认证
    public String VERTIFY = U_WEBSITE + "vertify2";

    //查询志愿者公告
    public String VOL_NOTICE = WEBSITE + "vol-notice";

    //查询志愿者活动列表
    public String VOL_LIST = WEBSITE + "vol-list";

    //社区活动打卡
    public String ACTION_QR = WEBSITE + "action-qr";

    //查看所有团队任务
    public String TEAM_LIST = WEBSITE + "team-list";

    //申请成为志愿者
    public String VOL_SIGN = WEBSITE + "vol-sign";

    //志愿者个人信息
    public String MINE_INFO = WEBSITE + "mine-info";

    //团队列表
    public String TEAM_RECRUIT = WEBSITE + "team-recruit";

    //创建志愿者团队
    public String VOL_TEAM = WEBSITE + "vol-team";

    //我的团队
    public String TEAM_TITLE = WEBSITE + "team-title";

    //团队成员列表
    public String USER_LIST = WEBSITE + "user-list";

    //发布个人任务
    public String VOL_TASK = WEBSITE + "vol-task";

    //申请成员列表
    public String USER_VERTIFY = WEBSITE + "user-vertify";

    //通过/拒绝申请加入
    public String VERTIFY_TRUE = WEBSITE + "vertify-true";

    //加入团队
    public String TEAM_JOIN = WEBSITE + "team-join";

    //退出团队
    public String TEAM_EXIT = WEBSITE + "team_exit";

    //志愿者接单(个人任务)
    public String VOL_ACCEPT = WEBSITE + "vol-accept";

    //志愿者开始打卡
    public String VOL_BEGIN = WEBSITE + "vol-begin";

    //查看我参加的活动
    public String VOL_MINE = WEBSITE + "vol-mine";

    //文件管理 上传图片返回为地址
    public String PHOTO = IP + "/file-manager/photo";

    //文件管理 上传音频返回地址
    public String MP3 = IP + "/file-manager/mp3";

    //志愿者结束打卡
    public String VOL_END = WEBSITE + "vol-end";

    //队长接取团队任务
    public String TEAM_ACCEPT = WEBSITE + "team-accept";

    //查询已接团队任务
    public String USER_SIGN = WEBSITE + "user-sign";

    //获取团队任务人数 以及参与时间
    public String TEAM_TASK_NUM = WEBSITE + "team-task-num";

    //队长确认报名结束
    public String TEAM_CONFIRM_SIGN = WEBSITE + "team-confirm-sign";

    //团队任务(可以打卡)列表
    public String TEAMS_MINE = WEBSITE + "teams-mine";

    //团队成员开始打卡
    public String TEAM_CARD_START = WEBSITE + "team-card-start";

    //团队成员结束打卡
    public String TEAM_CARD_END = WEBSITE + "team-card-end";

    //查询安全警报消息
    public String ALERM = WEBSITE +"alerm";

    //查看健康异常消息
    public String DATAABORT = WEBSITE +"dataAbort";

    //个人时间银行
    public String VOL_INFO = WEBSITE + "vol-info";

    //队长确认最终结束
    public String TEAM_CONFIRM_END = WEBSITE + "team-confirm-end";

    //查询个人兑换记录
    public String VOL_SEND = WEBSITE + "vol-send";

    //解散团队
    public String CANCEL_TEAM = WEBSITE + "cancel-team";

    //团队成员取消报名
    public String USER_CONFIRM_CANCEL = WEBSITE + "user-confirm-cancel";

    //取消个人任务
    public String VOL_CANCEL = WEBSITE + "vol-cancel";

    //取消团队任务
    public String TEAM_CONFIRM_CANCEL = WEBSITE + "team-confirm-cancel";

    //查看高龄补贴发放记录
    public String EVALUTION_SUBSIDY = WEBSITE + "evalution-subsidy";

    //根据imei号查询设备信息
    public String IMEI_BIND = WEBSITE + "imei-bind";

    //查询是否签约家庭医生
    public String APPLYDOCTOR = WEBSITE + "applyDoctor";

    //查询可以签约的家庭医生
    public String FAMILY_DOCTOR = WEBSITE + "family-doctor";

    //新增医单
    public String DOCTORORDER = WEBSITE + "doctorOrder";

    //查询个人医单
    public String HEALTH_ORDER = WEBSITE + "health-order";

    //用户确认医单完成
    public String SUCCESSORDER = WEBSITE + "successOrder";

    //查询收藏的医生列表
    public String COLLECTION_DOCTOR = WEBSITE + "collection-doctor";

    //关注医生
    public String FOLLOW = WEBSITE + "follow";

    //医单确认支付
    public String DOCTOR_PAY = WEBSITE + "doctor-pay";

    //取消医单
    public String DOCTOR_CANCEL = WEBSITE + "docotr-cancel";

    //医单评价
    public String APPRAISE = WEBSITE + "appraise";

    //意见反馈
    public String ADVICE = WEBSITE + "advice";

    //根据聊天id获取信息
    public String CHAT_USERINFO = C_IP + "/userinfo";

    //查询未读消息
    public String HISTORY = C_IP + "/history";

    //健康报告列表
    public String HEALTH_REPORT = WEBSITE+"health-report";

    //查询所有民政社区
    public String COMMITTEE_LIST =  IP + "/sysDict/committee-list";

    //绑定民政单位
    public String CIVILL =  WEBSITE+"civill";

    //查询适老化绑定状态
    public String CIVILL_BIND = WEBSITE +"civill-bind";

    //绑定记录列表
    public String CIVILL_RECORD = WEBSITE +"civill-record";

    //撤销申请民政单位
    public String CANCEL = WEBSITE +"cancel";



   //蓝牙登录(长桑)
    public static final String ENTERPRISELOGIN = "https://test-health.vita-course.com/gromit/entry/enterpriselogin";

    //上传标定参数

    //获取积分记录
    public String INTEGRAL = WEBSITE + "integral";


}
