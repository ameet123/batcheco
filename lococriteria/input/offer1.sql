CREATE external TABLE loco_charge( 
  ban              BIGINT, 
  subscriber_no    BIGINT, 
  finance_eff_date date, 
  partition_key    INT, 
  actv_bill_seq_no INT, 
  act_date         date, 
  feature_code string, 
  ftr_revenue_code string, 
  soc string, 
  actv_amt DECIMAL(6,2), 
  feature_group string, 
  feature_desc string, 
  ftr_med_desc string); 
  
CREATE external TABLE loco_adjustment( 
    ban              BIGINT, 
    subscriber_no    BIGINT, 
    finance_eff_date date, 
    partition_key    INT, 
    actv_bill_seq_no INT, 
    act_date         date, 
    feature_code string, 
    ftr_revenue_code string, 
    soc string, 
    actv_amt DECIMAL(6,2), 
    feature_group string, 
    feature_desc string, 
    ftr_med_desc string);