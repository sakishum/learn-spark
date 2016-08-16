一、 事件中心将从大数据平台接收的数据根据全触点提供规则过滤后，放到指定的topic中，供全触点取用
    目前支持4g换卡、网厅缴费、业务订购三个事件的场景
    业务说明：
    1. 4g换卡：只可能有用户过滤，没有字段条件
    2. 网厅缴费：有通过缴费金额(payment_fee)或缴费渠道(login_no)三字段中的一个为条件再加上客户群筛选
           a. 缴费金额(payment_fee)：只有在[start,end]这个区间选择
           b. 缴费渠道(login_no):单选
    3 业务订购:可以通过资费代码(prod_prcid)多选过滤
     
二、全触点将规则存入db2中方便
表结构刘文君确定
关键字段
{"ruleid":"123","eventid":"event_netpay","fields":"payment_fee >= 10","groupkey":"guser1","starttime":"2016-08-15 10:49:27","endtime":"2016-09-15 14:49:27" }
ruleid:规则id, 建议使用 "qcd_营销活动id"
eventid: 事件类型：event_netpay:网厅缴费,event_usim_change:4g换卡,event_busi_order:业务订购
fields：字段条件 “字段，操作符，值 ”三者用空格分开,目前都是单条件 比如：payment_fee range 50,100   login_no in aa,bb,cc,dd 注意值之间不要有空格
groupkey：如果有用户群过滤时，值为用户群在redis中存的key
starttime:规则开始时间
endtime:规则结束时间

事件中心收到规则后修改状态即可 


四、规则开始生效后，事件中心将过滤后的数据放入kafka中供全触点取用

一个事件一个topic?

输出消息格式：{k1=v1,k2=v2}：
所有输出都有：规则id(ruleid),事件类型(eventid)
4G换卡输出：用户号码(phone_no)，时间(date)
网厅缴费：用户号码(phone_no)，缴费金额(payment_fee),缴费渠道(login_no)，时间(date)
业务订购：用户号码(phone_no)，资费代码(prod_prcid)，时间(date)