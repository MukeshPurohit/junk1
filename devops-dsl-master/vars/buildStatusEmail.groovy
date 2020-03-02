def call(body)
{
	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    	body.delegate = config
    	body()
	def buildResult = config.buildResult
	def emailRecipients = config.emailRecipients
	mail (
		body: "Hi,\n\n${JOB_NAME} job with build number #${BUILD_NUMBER} is ${buildResult}.\nYou can find build log at ${BUILD_URL}\n\nRegards,\nDevops CoE Team",
		replyTo: 'mukeshp1@verifone.com',
		subject: "Jenkins: ${JOB_NAME} job with build number #${BUILD_NUMBER} is ${buildResult}",
		to: "${emailRecipients}"
		)
}
