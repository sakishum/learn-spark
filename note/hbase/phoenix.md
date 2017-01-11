## 导出数据到文件

```
!outputformat csv
!record data.csv
select * from system.catalog limit 10;
!record
!quit
```