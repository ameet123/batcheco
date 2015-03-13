select BAN, SUBSCRIBER_NO, FINANCE_EFF_DATE, PARTITION_KEY, ACTV_BILL_SEQ_NO, 
       ACT_DATE, FEATURE_CODE, SOC, SUM(ACTV_AMT) as sum_actv_amt
  from (
select BAN, SUBSCRIBER_NO, FINANCE_EFF_DATE, PARTITION_KEY, ACTV_BILL_SEQ_NO,
       ACT_DATE, FEATURE_CODE, SOC, ACTV_AMT
  from loco_charge
 where feature_group not in ('TF','VF','PF')
   and feature_code not in (
	'AMBMIN', 'AMBREV','CVAB','CVAT', 'CVBK', 'CVCL', 
	'CVDP', 'CVIC', 'CVMT', 'CVOC', 'CVRM', 'CVTL', 'DCK', 'DEPOST', 
	'GENDEP', 'LTPYM', 'LTPYMM', 'REFUND', 'RFNDEP', 'RSLTFE', 
	'TLRNCE', 'XMLPA','PAFM')
UNION ALL
select BAN, SUBSCRIBER_NO, FINANCE_EFF_DATE, PARTITION_KEY, ACTV_BILL_SEQ_NO, 
       ACT_DATE, FEATURE_CODE, SOC, ACTV_AMT
  from loco_adjustment 
 where feature_group not in ('TF','VF','PF')
   and feature_code not in (
        'AMBMIN', 'AMBREV','CVAB','CVAT', 'CVBK', 'CVCL',
        'CVDP', 'CVIC', 'CVMT', 'CVOC', 'CVRM', 'CVTL', 'DCK', 'DEPOST', 
        'GENDEP', 'LTPYM', 'LTPYMM', 'REFUND', 'RFNDEP', 'RSLTFE', 
        'TLRNCE', 'XMLPA','PAFM')
) a
 group by BAN, SUBSCRIBER_NO, FINANCE_EFF_DATE, PARTITION_KEY, 
          ACTV_BILL_SEQ_NO, ACT_DATE, FEATURE_CODE, SOC;