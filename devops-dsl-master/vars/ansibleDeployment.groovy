def call(body)
{
	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    	body.delegate = config
    	body()
	def deploymentRepoName = config.deploymentRepoName
	def playbookFilePath = config.playbookFilePath
	def inventoryFileName = config.inventoryFileName
	def deploymentRepoPath = config.deploymentRepoPath
	def applicationName = config.applicationName
	git credentialsId: 'mukeshp1_bitbucket_cred',url: "https://<bitbucketURL>/bitbucket/${deploymentRepoPath}"
	sh """
	  /usr/bin/ansible-playbook devDeployment.yml -i devInventory.yml
	"""

	/* ansiblePlaybook  disableHostKeyChecking: true, 
				installation: 'ansible', 
				inventory: "${inventoryFileName}", 
				playbook: "${playbookFilePath}",
				extras: "srcLocation=${WORKSPACE}/target/SimpleWebApplication.war workspace=${WORKSPACE}"
		*/		

}
