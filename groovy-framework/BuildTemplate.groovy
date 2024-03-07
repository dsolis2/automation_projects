import jxl.*;
import jxl.write.*

fileName = "C:\\SOAP\\JSON\\System_Test_Cases.xls"
//inputFileName = "D:\\PRE\\run\\System_Test_Cases_Groovy_Input.xls"
ignoreTestSuites="Automation_Test_Suite"

WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
workbook.createSheet("Sheet1",0);
def sheet = workbook.getSheet(0)
sheet.addCell(new Label(0,0,"Test Suite"))
sheet.addCell(new Label(1,0,"Test Case"))
sheet.addCell(new Label(2,0,"Test Step"))
		
testSuites = testRunner.testCase.testSuite.project.getTestSuiteList()
row_num=1
testSuite:
for(ts in testSuites)
{
	for(ts_ignore in ignoreTestSuites.split(","))
	{
		if(ts.name==ts_ignore){continue testSuite}
	}
	testCases = testRunner.testCase.testSuite.project.testSuites[ts.name].getTestCaseList()
	sheet.addCell(new Label(0,row_num,ts.name))
			
	for(tc in testCases)
	{
		sheet.addCell(new Label(0,row_num,ts.name))
		sheet.addCell(new Label(1,row_num,tc.name))
		testSteps = testRunner.testCase.testSuite.project.testSuites[ts.name].testCases[tc.name].getTestStepList()
				
		for(tstep in testSteps)
		{			
			sheet.addCell(new Label(0,row_num,ts.name))
			sheet.addCell(new Label(1,row_num,tc.name))
			sheet.addCell(new Label(2,row_num,tstep.name))
			row_num++
			
			//log.info tstep.name
		}
	}
	
}

for(int x=0;x<sheet.getColumns();x++)
{	
	CellView cell=sheet.getColumnView(x);
	cell.setSize(9000)	
	sheet.setColumnView(x,cell);			
}

workbook.write()
workbook.close()

