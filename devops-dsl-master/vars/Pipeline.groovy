def call(body)
{
	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
   	body.delegate = config
    	body()
    	def applicationName = config.applicationName ?: 'SAMPLE'
	def buildNode = config.buildNode ?: 'slave2'
	def mvnGoals = config.mvnGoals ?: 'clean install'            
	def executeSonar = config.executeSonar ?: 'YES'
	def masterGoals = 'clean install'
	// Coverage and UnitTest reports parameters
	def junitReportLocation = config.junitReportLocation ?: 'target/surefire-reports/*.xml'
	def jacocoClassPattern = config.jacocoClassPattern ?: '**/classes'
	def jacocoExecPattern = config.jacocoExecPattern ?: '**/**.exec'
	def jacocoSourceInclusionPattern = config.jacocoSourceInclusionPattern ?: '**/*.java'
	def jacocoSourcePattern = config.jacocoSourcePattern ?: '**/src/main/java'
	def emailRecipientsList = config.emailRecipientsList
	node("${buildNode}"){
		try {
		        currentBuild.result = 'SUCCESS'
        		stage("Checkout source code"){
				//step([$class: 'WsCleanup'])
				checkout scm
        		}
        		stage("Maven build"){	
				if("${BRANCH_NAME}" != 'master'){
					maven{
						mavenGoals = "${mvnGoals}"
					}
				}
				else{
					maven {
						mavenGoals = "${masterGoals}"
					}
				}
           		}
			if(("${BRANCH_NAME}" == 'master') || (!("${mvnGoals}" =~ "-DskipTests"))){
                		stage("Unit Tests and Coverage"){ 
                                	CoverageReports{
						junitResultLocation = "${junitReportLocation}"
						classPattern = "${jacocoClassPattern}"
						execPattern = "${jacocoExecPattern}"
						sourceInclusionPattern = "${jacocoSourceInclusionPattern}"
						sourcePattern = "${jacocoSourcePattern}"
					}
                   		} // End of Unit Tests and Coverage
               		} // End of If Branch check for report publish
        		if("${executeSonar}" == 'YES' || "${BRANCH_NAME}" == 'master'){
		   		stage("Sonar Scan"){
		        		sonarscan{
			        		applicationName = config.applicationName
			        		projectName = config.sonarProjectName
				        	projectKey = config.sonarProjectKey
			        		projectVersion = config.sonarProjectVersion
			        		sonarLanguage = config.sonarProjectLanguage
			        		sonarSources = config.sonarSources
		        		}
		    		} // End of Sonarscan stage
				stage("Sonar Quality gate Check")
				{
					timeout(time: 1, unit: 'HOURS')
					{
						def qualityGate = waitForQualityGate()
						if(qualityGate.status != 'OK')
						{
							error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
						}
					}
				}// End of QualityGate check stage
			}  // End of sonarExecution evaluation IF statement
			stage('Upload to Nexus') {
			    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'vcs_Devops-POC_maven_releases', packages: []
			}
			if("${BRANCH_NAME}" == 'master')
			{
				stage('Deployment')
				{
				
						ansibleDeployment
						{
							deploymentRepoName = config.deploymentRepo_name
							playbookFilePath = config.ansiblePlaybookfileName
					      	inventoryFileName = config.ansibleInventory_FileName
					      	deploymentRepoPath = config.deploymentRepo_Path
							applicationName = config.application_Name
						}
					
				}
			}
		} // End of try block
		catch (Exception err)
		{
            		currentBuild.result = 'FAILURE'
					echo 'Build failed, We should sound the Devops team...!'
		}
		finally
		{
			stage("Email Notification")
			{
			    	def buildresult = "${currentBuild.result}"
				buildStatusEmail
				{
					buildResult = "${buildresult}"
					emailRecipients = "${emailRecipientsList}"
				}
			}
		}
	} // End of Node Block	
}
