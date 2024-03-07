import com.eviware.soapui.model.testsuite.TestRunner.Status
import groovy.json.JsonSlurper 
import java.util.Date;
import java.io.File;
import java.util.*;
import java.lang.*; 
import jxl.*;
import jxl.write.*
import com.soapui.BuildJsonPath.*;


inputFileName = "C:\\SOAP\\JSON\\System_Test_Cases.xls"
//inputFileName = "D:\\PRE\\run\\System_Test_Cases_Groovy_Input.xls"

class BuildJsonPath
{
	def inputFileName
	def outputFileName
	
	def BuildJsonPath(def inputFileName,def outputFileName)
	{
		this.inputFileName=inputFileName
		this.outputFileName=outputFileName		
	}

	def run(def log,def testRunner,def context)
	{
		def wb = Workbook.getWorkbook(new File(inputFileName))
		WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName),wb)
		def sheet1 = wb.getSheet(0)
		def sheet_r = workbook.getSheet(0)
		sheet_r.addCell(new Label(3,0,"Validation File"))
		sheet_r.addCell(new Label(4,0,"RUN STATE"))
		def row_count = sheet1.getRows()
		for(def i=1; i< row_count; i++)
		{
			def test_suite = sheet1.getCell(0,i).getContents()
			if(test_suite.isEmpty()){continue}
			def test_case = sheet1.getCell(1,i).getContents()
			def test_step = sheet1.getCell(2,i).getContents()
						
			def tc = testRunner.testCase.testSuite.project.testSuites[test_suite].testCases[test_case]
			
			if(tc==null)
			{
				log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} -> Test Case is not present"
				sheet_r.addCell(new Label(3,i,"Test Case is not present"))
				continue	
			}
			
			def ts = tc.testSteps[test_step]
			
			if(ts==null)
			{
			log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} -> Test Step is not present"
			sheet_r.addCell(new Label(3,i,"Test Step is not present"))
			continue	
			}

			def response
			try{
				def runner = ts.run(testRunner, context)
				response= runner.response.contentAsString
			}
			catch(Exception expObj) {
				//log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} has Exception ${expObj}"
				sheet_r.addCell(new Label(3,i,expObj.toString()))	
				continue
			}
			
			try{
			JsonPathBuilder jsonpathbuilder = new JsonPathBuilder(response.toString())
			def csvFileName = "${new File(inputFileName).getParent()}\\validation\\${test_case} && ${test_step}.csv"
			log info csvFileName
			jsonpathbuilder.createCSVFile(csvFileName)
			log.info "JSON Path CSV File: ${new File(csvFileName).getName()} is created under validation folder"
			sheet_r.addCell(new Label(3,i,"validation\\${new File(csvFileName).getName()}"))
			}
			catch(Exception expObj)
			{
				log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} has Exception ${expObj}"
				sheet_r.addCell(new Label(3,i,expObj.toString()))
				continue
			}
			log.info "RUN STATUS -> ${test_suite} -> ${test_case} -> ${test_step} -> ${i}/${(row_count-1)} is Completed"
			//def csvResponse = jsonpathbuilder.returnAsCSV()
			//context.fileReader = new BufferedReader(new StringReader(csvResponse))
			//def rowsData = context.fileReader.readLines()

			sheet_r.addCell(new Label(4,i,"TRUE"))
						
		}

			for(int x=0;x<sheet_r.getColumns();x++)
			{	
				CellView cell=sheet_r.getColumnView(x);
				cell.setSize(9000)	
				sheet_r.setColumnView(x,cell);			
			}
			workbook.write()
			workbook.close()
	}	
}

//Groovy Script -----
def path=inputFileName.substring(0, inputFileName.lastIndexOf("\\"));

log.info path
if(!new File(path).exists())
{	
	log.info "Input File Path \"${path}\" does not exists and hence exiting the program"
	return
}
def validationFolder = new File("${path}\\validation")
if(!validationFolder.exists())
{
	validationFolder.mkdirs()
}
def fileNameSplit = new File(inputFileName.toString()).getName().split("\\.")
def outputFileName = "${path}\\${fileNameSplit[0]}_Groovy_Input.${fileNameSplit[1]}"
def buildjsonpath = new BuildJsonPath(inputFileName,outputFileName)
try{
buildjsonpath.run(log,testRunner,context)
}
catch(Exception expObj)
{
	log.info expObj
}
