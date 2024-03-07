endPoint = "https://pre-qa.xxx.net"
ignoreTestSuites="Automation_Test_Suite"
		
testSuites = testRunner.testCase.testSuite.project.getTestSuiteList()

testSuite:
for(ts in testSuites)
{
	for(ts_ignore in ignoreTestSuites.split(","))
	{
		if(ts.name==ts_ignore){continue testSuite}
	}
	testCases = testRunner.testCase.testSuite.project.testSuites[ts.name].getTestCaseList()
				
	for(tc in testCases)
	{
		testSteps = testRunner.testCase.testSuite.project.testSuites[ts.name].testCases[tc.name].getTestStepList()
				
		for(tstep in testSteps)
		{			
			tstep.getHttpRequest().setEndpoint(endPoint)
			log.info "${tstep.name} is updated with ${endPoint}"
			//log.info tstep.name
		}
	}
	
}
