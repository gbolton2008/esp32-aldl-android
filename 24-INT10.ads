//--------------------------------------------------------------------------------------
// Created by TunerPro. Hand editing is *not* recommended or supported.
//--------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------
//--------------------------------- HEADER ------------------------------------
//--------------------------------------------------------------------------------------

{
	fDefFrmtVers             =1.21;
	strDefVersion            =Version 1.0;
	strDefTitle              =A29 $24;
	strAuthor                =Robert Saar;
	strEngine                =2.8MPFIHO L44;
	strYear                  =1985-1988;
	strVINCode               =9;
	strCodeMask              =$24 $24A;
	strComments              =use 10K resistor. questions comments should be directed to robertisaar@yahoo.com;
	iBaud                    =160;
	dwFlags                  =0x00000000;
	dwCSID                   =0x0000BCE4;
	btNumDumpRequests        =1;

	strCommandName           =Data Transfer;
	rgbtCommand              =;
	iTotalBytesInCommand     =0;
	bChecksumCommand         =0;
	iNumBytesInPayload       =25;
	iNumBytesBeforePayload   =-1;
	bMaster                  =1;
	bMonitor                 =1;
	iChainTo                 =-1;
}

//--------------------------------------------------------------------------------------
//---------------------------------- DASH -------------------------------------
//--------------------------------------------------------------------------------------

{
	dwItemType               =6;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =0;

	btNumGauges              =6;
	strIDsDisplayed          =0,0,0,0,0,0,;
	btNumMonitors            =4;
	strMonsDisplayed         =0,0,0,0,;
}

//--------------------------------------------------------------------------------------
//--------------------------------- VALUES ------------------------------------
//--------------------------------------------------------------------------------------

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =1;
	bVisible                 =1;
	dwUniqueID               =54;

	btByteNumber             =0;
	btMessageNumber          =0;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             ======Important=====;
	strUnitLabel             =Units;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =4;

	btByteNumber             =6;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =3;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =Vehicle Speed;
	strUnitLabel             =MPH;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =7;

	btByteNumber             =8;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =25.000000;
	dOffset                  =0.000000;
	strItemTitle             =Engine Speed;
	strUnitLabel             =RPM;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =8;

	btByteNumber             =9;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.019608;
	dOffset                  =0.000000;
	strItemTitle             =TPS;
	strUnitLabel             =Volts;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =1;
	bVisible                 =1;
	dwUniqueID               =55;

	btByteNumber             =0;
	btMessageNumber          =0;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             ======Temps=====;
	strUnitLabel             =Units;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =2;

	btByteNumber             =5;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.750000;
	dOffset                  =-40.000000;
	strItemTitle             =Coolant Temp;
	strUnitLabel             =C;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =3;

	btByteNumber             =5;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =1.350000;
	dOffset                  =-40.000000;
	strItemTitle             =Coolant Temp;
	strUnitLabel             =F;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =49;

	btByteNumber             =23;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =6;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =MAT;
	strUnitLabel             =C;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =52;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =50;

	btByteNumber             =23;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =6;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =MAT;
	strUnitLabel             =F;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =53;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =1;
	bVisible                 =1;
	dwUniqueID               =56;

	btByteNumber             =0;
	btMessageNumber          =0;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             ======Air/Fuel=====;
	strUnitLabel             =Units;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =5;

	btByteNumber             =7;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.019608;
	dOffset                  =0.000000;
	strItemTitle             =MAP;
	strUnitLabel             =Volts;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =6;

	btByteNumber             =7;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.369000;
	dOffset                  =10.354000;
	strItemTitle             =MAP;
	strUnitLabel             =kPa;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =45;

	btByteNumber             =19;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =3;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =BLM;
	strUnitLabel             =;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =9;

	btByteNumber             =10;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =3;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =INT;
	strUnitLabel             =;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =10;

	btByteNumber             =11;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =4.440000;
	dOffset                  =0.000000;
	strItemTitle             =O2 Sensor;
	strUnitLabel             =mV;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =46;

	btByteNumber             =20;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =3;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =Rich/Lean Transitions;
	strUnitLabel             =Crosses;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =51;

	btByteNumber             =24;
	btMessageNumber          =1;
	dwItemSizeBits           =16;
	dwOperation              =0;
	dFactor                  =0.015259;
	dOffset                  =0.000000;
	strItemTitle             =BPW;
	strUnitLabel             =mS;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =65536;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =1;
	bVisible                 =1;
	dwUniqueID               =57;

	btByteNumber             =0;
	btMessageNumber          =0;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             ======Misc=====;
	strUnitLabel             =Units;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =48;

	btByteNumber             =22;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.392157;
	dOffset                  =0.000000;
	strItemTitle             =EGR Duty Cycle;
	strUnitLabel             =%;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =47;

	btByteNumber             =21;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.351563;
	dOffset                  =0.000000;
	strItemTitle             =Spark Advance;
	strUnitLabel             =*;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =44;

	btByteNumber             =18;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =0;
	dFactor                  =0.100000;
	dOffset                  =0.000000;
	strItemTitle             =Battery Voltage;
	strUnitLabel             =Volts;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

{
	dwItemType               =1;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =1;

	btByteNumber             =4;
	btMessageNumber          =1;
	dwItemSizeBits           =8;
	dwOperation              =3;
	dFactor                  =1.000000;
	dOffset                  =0.000000;
	strItemTitle             =IAC Position;
	strUnitLabel             =Steps;
	dwAlarmHigh              =255;
	bAlarmHighENable         =0;
	dwAlarmLow               =0;
	bAlarmLowEnable          =0;
	iRangeHigh               =255;
	iRangeLow                =0;
	iLookupTableIndex        =-1;
}

//--------------------------------------------------------------------------------------
//---------------------------------- BITS -------------------------------------
//--------------------------------------------------------------------------------------

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =1;
	bVisible                 =1;
	dwUniqueID               =11;

	btByteNumber             =0;
	btMessageNumber          =0;
	btBitNumber              =0;
	strItemTitle             ======Codes=====;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =1;
	strBitClearTitle         =0;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =19;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =7;
	strItemTitle             =12 Crank Sensor/System Check;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =18;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =6;
	strItemTitle             =13 O2 Sensor;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =17;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =5;
	strItemTitle             =14 Coolant High;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =16;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =4;
	strItemTitle             =15 Coolant Low;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =15;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =3;
	strItemTitle             =21 TPS High;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =14;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =2;
	strItemTitle             =22 TPS Low;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =13;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =1;
	strItemTitle             =23 MAT Low;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =12;

	btByteNumber             =12;
	btMessageNumber          =1;
	btBitNumber              =0;
	strItemTitle             =24 VSS;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =25;

	btByteNumber             =13;
	btMessageNumber          =1;
	btBitNumber              =7;
	strItemTitle             =25 MAT High;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =24;

	btByteNumber             =13;
	btMessageNumber          =1;
	btBitNumber              =5;
	strItemTitle             =32 EGR;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =23;

	btByteNumber             =13;
	btMessageNumber          =1;
	btBitNumber              =4;
	strItemTitle             =33 MAP High;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =22;

	btByteNumber             =13;
	btMessageNumber          =1;
	btBitNumber              =3;
	strItemTitle             =34 MAP Low;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =21;

	btByteNumber             =13;
	btMessageNumber          =1;
	btBitNumber              =2;
	strItemTitle             =35 IAC;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =20;

	btByteNumber             =13;
	btMessageNumber          =1;
	btBitNumber              =0;
	strItemTitle             =42 EST;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =32;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =7;
	strItemTitle             =43 Knock Sensor;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =31;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =6;
	strItemTitle             =44 O2 Lean;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =30;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =5;
	strItemTitle             =45 O2 Rich;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =29;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =4;
	strItemTitle             =51 PROM;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =28;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =3;
	strItemTitle             =52 CAL-PACK;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =27;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =2;
	strItemTitle             =53 Battery Voltage High;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =26;

	btByteNumber             =14;
	btMessageNumber          =1;
	btBitNumber              =0;
	strItemTitle             =55 ADU;
	bAlarmSetEnable          =1;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ERROR;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =1;
	bVisible                 =1;
	dwUniqueID               =33;

	btByteNumber             =0;
	btMessageNumber          =0;
	btBitNumber              =0;
	strItemTitle             ======Misc=====;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =1;
	strBitClearTitle         =0;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =34;

	btByteNumber             =15;
	btMessageNumber          =1;
	btBitNumber              =1;
	strItemTitle             =BLM Enable;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =YES;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =35;

	btByteNumber             =15;
	btMessageNumber          =1;
	btBitNumber              =3;
	strItemTitle             =Quasi Pulse;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =YES;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =36;

	btByteNumber             =15;
	btMessageNumber          =1;
	btBitNumber              =4;
	strItemTitle             =Async Pulse;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =YES;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =37;

	btByteNumber             =15;
	btMessageNumber          =1;
	btBitNumber              =6;
	strItemTitle             =Rich/Lean;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =RICH;
	strBitClearTitle         =LEAN;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =38;

	btByteNumber             =15;
	btMessageNumber          =1;
	btBitNumber              =7;
	strItemTitle             =Loop Status;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =1;
	strBitSetTitle           =CLOSED;
	strBitClearTitle         =OPEN;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =39;

	btByteNumber             =16;
	btMessageNumber          =1;
	btBitNumber              =5;
	strItemTitle             =A/C;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =;
	strBitClearTitle         =ENABLED;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =40;

	btByteNumber             =16;
	btMessageNumber          =1;
	btBitNumber              =7;
	strItemTitle             =P/N Switch;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =PARK/NEUTRAL;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =41;

	btByteNumber             =17;
	btMessageNumber          =1;
	btBitNumber              =0;
	strItemTitle             =A/C Clutch;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ENABLED;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =42;

	btByteNumber             =17;
	btMessageNumber          =1;
	btBitNumber              =2;
	strItemTitle             =TCC;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =LOCKED;
	strBitClearTitle         =;
}

{
	dwItemType               =2;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =43;

	btByteNumber             =17;
	btMessageNumber          =1;
	btBitNumber              =5;
	strItemTitle             =Power Steering Cramp;
	bAlarmSetEnable          =0;
	bAlarmNotSetEnable       =0;
	strBitSetTitle           =ACTIVE;
	strBitClearTitle         =;
}

//--------------------------------------------------------------------------------------
//---------------------------- LOOKUP TABLES ----------------------------------
//--------------------------------------------------------------------------------------

{
	dwItemType               =5;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =52;

	btDataType               =2;
	wTableSize               =256;
	wIndexSize               =4;
	strTableName             =MAT C;
	dwReserved               =0;
	dwReserved               =0;
	pbtData                  =0, 200.00
				 12, 150.00
				 13, 145.00
				 14, 140.00
				 16, 135.00
				 18, 130.00
				 21, 125.00
				 23, 120.00
				 26, 115.00
				 30, 110.00
				 34, 105.00
				 39, 100.00
				 44, 95.00
				 50, 90.00
				 56, 85.00
				 64, 80.00
				 72, 75.00
				 81, 70.00
				 92, 65.00
				 102, 60.00
				 114, 55.00
				 126, 50.00
				 139, 45.00
				 152, 40.00
				 165, 35.00
				 177, 30.00
				 189, 25.00
				 199, 20.00
				 209, 15.00
				 218, 10.00
				 225, 5.00
				 231, 0.00
				 237, -5.00
				 241, -10.00
				 245, -15.00
				 247, -20.00
				 250, -25.00
				 251, -30.00
				 255, -40.00;
}

{
	dwItemType               =5;
	strItemComments          =<Comments>;
	bSeparator               =0;
	bVisible                 =1;
	dwUniqueID               =53;

	btDataType               =2;
	wTableSize               =256;
	wIndexSize               =4;
	strTableName             =MAT F;
	dwReserved               =0;
	dwReserved               =0;
	pbtData                  =0, 392.00
				 12, 302.00
				 13, 293.00
				 14, 284.00
				 16, 275.00
				 18, 266.00
				 21, 257.00
				 23, 248.00
				 26, 239.00
				 30, 230.00
				 34, 221.00
				 39, 212.00
				 44, 203.00
				 50, 194.00
				 56, 185.00
				 64, 176.00
				 72, 167.00
				 81, 158.00
				 92, 149.00
				 102, 140.00
				 114, 131.00
				 126, 122.00
				 139, 113.00
				 152, 104.00
				 165, 95.00
				 177, 86.00
				 189, 77.00
				 199, 68.00
				 209, 59.00
				 218, 50.00
				 225, 41.00
				 231, 32.00
				 237, 23.00
				 241, 14.00
				 245, 5.00
				 247, -4.00
				 250, -13.00
				 251, -22.00
				 255, -40.00;
}

