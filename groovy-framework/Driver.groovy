import com.eviware.soapui.model.testsuite.TestRunner.Status
import groovy.json.JsonSlurper 
import java.util.Date;
import java.io.File;
import java.util.*;
import java.lang.*; 
import jxl.*;
import jxl.write.*
import com.soapui.BuildJsonPath.*
import soapui.com.jayway.jsonpath.*
import static org.junit.Assert.*;
import com.jayway.jsonpath.ExcelToHtml.HTML;
import com.zephyr.update.client.*;


/*initializing the script input variable values from run batch file*/

inputFileName = testRunner.testCase.testSuite.project.getPropertyValue("inputFileName")

parallelRun = testRunner.testCase.testSuite.project.getPropertyValue("parallelRun")

/* Key in the json path to be ignored. for mutiple path, give it comma seperated */
ignoreJsonPathValidation= testRunner.testCase.testSuite.project.getPropertyValue("ignoreJsonPathValidation")

if(inputFileName==null)
{
	log.info "inputFileName is not present in the property. Pleae set the property"
	return
}

if(parallelRun==null)
{
	log.info "parallelRun is not present in the property. Pleae set the property"
	return
}

if(ignoreJsonPathValidation==null)
{
	log.info "ignoreJsonPathValidation is not present in the property. Pleae set the property"
	return
}


/* Main Driver Class */

class Driver
{
	def static ignoreJsonPathValidation
	def static assertionList = []	
	def static sheet_i
	def static sheet_r
	def static row_count
	def static inputPath
	def static validation_row_num
	def static sheet_v
	public static Object Lock1 = new Object();	  	
	ZephyrClient zephyr;
	
	def Driver()
	{
		
	}

	/* run method -> extracts the test data from spreadsheet, runs the API, validates the response, report in csv and html */
	
	def run(def log,def testRunner,def context,def i)
	{
			
			/* Extracting the test case/data information from spreadsheet into groovy objects */
			
			def test_suite = sheet_i.getCell(0,i).getContents()
			if(test_suite.isEmpty()){return}
			def test_case = sheet_i.getCell(1,i).getContents()
			def test_step = sheet_i.getCell(2,i).getContents()
			def validationCsvFilePath = "${inputPath}\\${sheet_i.getCell(3,i).getContents()}"
			def run_state = sheet_i.getCell(4,i).getContents()						
			def issue_key = sheet_i.getCell(5,i).getContents()
			def fix_version = sheet_i.getCell(6,i).getContents()
			def test_cycle = sheet_i.getCell(7,i).getContents()
			def result = "Pass"

			def t_suite = testRunner.testCase.testSuite.project.testSuites[test_suite]
			if (t_suite==null)
			{
				log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} -> Test Suite is not present"
				sheet_r.addCell(new Label(8,i,"Error"))
				sheet_r.addCell(new Label(9,i,"Test Suite is not present"))
				return	
			}
									
			def tc = t_suite .testCases[test_case]
			
			if(tc==null)
			{
				log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} -> Test Case is not present"
				sheet_r.addCell(new Label(8,i,"Error"))
				sheet_r.addCell(new Label(9,i,"Test Case is not present"))
				return	
			}
			
			def ts = tc.testSteps[test_step]
			
			if(ts==null)
			{
			log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} -> Test Step is not present"
			sheet_r.addCell(new Label(8,i,"Error"))
			sheet_r.addCell(new Label(9,i,"Test Step is not present"))
			return	
			}

			def response
			
			/* Running the API */
			def runner
			try{
				synchronized (Lock1) {	
				runner = ts.run(testRunner, context)
				}
		
				response= runner.response.contentAsString
				//log.info "Response is ${response}"
			}
			catch(Exception expObj) {
				log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} has Exception ${expObj}"
				sheet_r.addCell(new Label(8,i,"Error"))
				sheet_r.addCell(new Label(9,i,expObj.toString()))
				result="Fail"
				return
			}			
			
			log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} Request is run and JSON Validation in progress"

			if(!new File(validationCsvFilePath).exists())
			{
				log.info "Skipping the validation and validation JSON File: ${new File(validationCsvFilePath).getName()} does not exists."
				return
			}
			
							
			/* Parsing JSON response returned by API */
			def jsonParse
			try{
			jsonParse = new JsonParse(response)
			}
			catch(Exception expObj)
			{
				log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} has Exception ${expObj}"
				sheet_r.addCell(new Label(8,i,"Error"))
				sheet_r.addCell(new Label(9,i,expObj.toString()))
				return	
			}

			/* Reading Expected Results and JSON Path from validation csv files */
			context.fileReader = new BufferedReader(new FileReader(validationCsvFilePath))
			def rowsData = context.fileReader.readLines()
			def rowsize = rowsData.size()
			jsonpath:
		     for(def j=1; j< rowsize;  j++)
    			{
    				def rowdata = rowsData[j]
    				//Splitting the string to 2 based on comma delimiter overcoming csv restriction
    				String[] data = rowdata.split(",",2)
    				data[0]=data[0].replaceAll("=","")
    				data[0]=data[0].replaceAll("\"","")
    				data[1]=data[1].replaceAll("=","")
    				data[1]=data[1].replaceAll("\"","")
    				for (ignore in ignoreJsonPathValidation.split(","))
    				{
    					if(data[0]==ignore){continue jsonpath}
    				}			
    				
    				def json_path = data[0]
    				def expected_result = data[1]
    				def actual_result
    				
    				/* Write validation results into Result spreadsheet	*/
    				sheet_v.addCell(new Label(0,validation_row_num,test_step))
    				sheet_v.addCell(new Label(1,validation_row_num,json_path))
    				sheet_v.addCell(new Label(2,validation_row_num,expected_result))
    				    				
    				/* Retriving the Json value from response using JSON Path expression */
    				try{
    					 actual_result = jsonParse.getJsonValue(json_path)    					 
    				}
    				catch(Exception expObj)
    				{
    					sheet_v.addCell(new Label(4,validation_row_num,"Exception"))
    					sheet_v.addCell(new Label(5,validation_row_num,expObj.toString()))
    					log.info "JSON Validation -> ${test_step} -> ${json_path} has Exception: ${expObj}"
    					return
    				}
    				    				
    				sheet_v.addCell(new Label(3,validation_row_num,actual_result))
    				
    				if(expected_result==actual_result)
    				{
    					sheet_v.addCell(new Label(4,validation_row_num,"Pass"))
    					log.info "JSON Validation -> ${test_step} -> ${json_path} -> Pass"    					
    				}
    				else
    				{
    					result="Fail"
    					sheet_v.addCell(new Label(4,validation_row_num,"Fail"))
    					log.info "JSON Validation -> ${test_step} -> ${json_path} -> Fail. Expected_Result:(${expected_result}) but Actual_Result:(${actual_result})"    					
    				}
    				validation_row_num++
    			}			
			
			/* Updating Results into Zephyr for JIRA*/
			TestResult zephyrResult = zephyr.createTest()
			zephyrResult.setIssuekey(issue_key)
			zephyrResult.setProjectKey("PRE")
			zephyrResult.setFixVersions(fix_version)
			zephyrResult.setTestCycle(test_cycle)
			zephyrResult.setTestResult(result)
			zephyrResult.updateResult()	
			sheet_r.addCell(new Label(8,i,result))
			assertionList.add(result)
			log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} is Complete"					

			
	}	
}
/* This class is for handling multithreading -> for running all test cases in parallel mode */

class MultiThreading extends Thread {

		private Thread t;
		private String threadName;
		def log; 
		def driver;
		def context; 
		def testRunner
		def i
	  	
		MultiThreading(String threadName,def log,def testRunner,def context,def driver,def i)
		{
			this.threadName=threadName
			this.log=log
			this.context=context
			this.testRunner=testRunner
			this.driver=driver
			this.i=i
		}

		@Override
		public void run()
		{
			log.info("Running the thread for the Input File: " +  threadName);
			try{
				driver.run(log,testRunner,context,i)		
			}
			catch(Exception expObj)
			{
				log.info "File: ${threadName} Error Occured: ${expObj}"
			}
		}

		public Thread returnThread()
		{
			return t;
		}
	
		public void start ()
	   	{
	   	   //log.info("Starting Thread for the input File " +  threadName );	     
	         t = new Thread (this, threadName);
	         t.start ();	         
	   	}
	    
   
}


/* Groovy Script -> Initialize the variables, creates object of a Driver class and call run method(single/multithreading) */
 ArrayList<Thread> threadList = new ArrayList<Thread>();

 def path=inputFileName.substring(0, inputFileName.lastIndexOf("\\"));
 if(!new File(path).exists())
 {	
	log.info "Input File Path \"${path}\" does not exists and hence exiting the program"
	return
 }
 def fileNameSplit = new File(inputFileName.toString()).getName().split("\\.")
 def outputFileName = "${path}\\${fileNameSplit[0]}_Results.${fileNameSplit[1]}"
 
 def wb = Workbook.getWorkbook(new File(inputFileName))
 WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName),wb)

 Driver.ignoreJsonPathValidation=ignoreJsonPathValidation
 Driver.sheet_i= wb.getSheet(0)
 Driver.sheet_r= workbook.getSheet(0)
 Driver.sheet_r.addCell(new Label(8,0,"Test Result"))
 Driver.sheet_r.addCell(new Label(9,0,"Exception"))
 Driver.sheet_v = workbook.createSheet("JSON_Validation",1)
 Driver.sheet_v.addCell(new Label(0,0,"Test Step"))
 Driver.sheet_v.addCell(new Label(1,0,"JSON Path"))
 Driver.sheet_v.addCell(new Label(2,0,"Expected Result"))
 Driver.sheet_v.addCell(new Label(3,0,"Actual Result"))
 Driver.sheet_v.addCell(new Label(4,0,"Result"))
 Driver.sheet_v.addCell(new Label(5,0,"Exception"))
 Driver.row_count = Driver.sheet_r.getRows()
 Driver.inputPath = new File(inputFileName).getParent().toString()
 Driver.validation_row_num=1 

 def driver = new Driver()

/* For Multi threading */
 if(parallelRun=="true") {
 for(def i=1; i< Driver.row_count; i++)
 {
 
 def run_state = Driver.sheet_i.getCell(4,i).getContents()
 
 if (run_state.equalsIgnoreCase("FALSE"))
 {
	continue
 }
 
 try{

  def thread = new MultiThreading("thread${i}",log,testRunner,context,driver,i)
  thread.start()
  threadList.add(thread.t)
  //driver.run(log,testRunner,context,i)
 }
 catch(Exception expObj)
 { 
 	log.info expObj
 }			
 }

 for(Thread t: threadList)
{
	try {
		t.join();
		//System.out.println(", status = " + t.isAlive());
		}
	catch (Exception expObj) {
		log.info e
		}
 }
 }

/* For sequential thread */
else {
 for(def i=1; i< Driver.row_count; i++)
 {
 	   		  	
 def run_state = Driver.sheet_i.getCell(4,i).getContents()
 
 if (run_state.equalsIgnoreCase("FALSE"))
 {
	continue
 }

 	
 try{
	driver.run(log,testRunner,context,i)
 }
 catch(Exception expObj)
 { 
 	log.info expObj
 }			
 }

 }

 for(int x=0;x<Driver.sheet_r.getColumns();x++)
 {	
 CellView cell=Driver.sheet_r.getColumnView(x);
 cell.setSize(9000)	
 Driver.sheet_r.setColumnView(x,cell);			
 }
 
 for(int x=0;x<Driver.sheet_v.getColumns();x++)
 {	
 CellView cell=Driver.sheet_v.getColumnView(x);
 cell.setSize(9000)	
 Driver.sheet_v.setColumnView(x,cell);			
 }
 
 workbook.write()
 workbook.close()
 
 /* creating html reports */
 
 def html = new HTML();
 html.createHTMLFile(new File(outputFileName).getAbsolutePath());

 File file = new File("${path}\\result.txt")
 file.write("");
 Driver.assertionList.each { 
	file << "$it\n"
 }

 //assertFalse (Driver.assertionList.contains("Fail"))