规则格式：
{"ruleid":"123","eventid":"event_netpay","fields":"payment_fee >= 10","groupkey":"guser1","starttime":"2016-08-15 10:49:27","endtime":"2016-09-15 14:49:27" }


ruleid:建议使用 "qcd_营销活动id"
eventid: event_netpay:网厅缴费,event_usim_change:4g换卡,event_busi_order:业务订购
fields：字段条件 “字段，操作符，值 ”三者用空格分开,目前都是单条件 比如：payment_fee range 50,100,  login_no in aa,bb,cc,dd
groupkey：如果有用户群过滤时，值为用户群在redis中存的key
starttime:规则开始时间
endtime:规则结束时间