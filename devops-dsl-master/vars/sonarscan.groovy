def call(body)
{
	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    	body.delegate = config
    	body()
    	def applicationName = config.applicationName
  	def projectName = config.projectName
      	def projectKey = config.projectKey
	def projectVersion = config.projectVersion
       	def sonarLanguage = config.sonarLanguage
       	def sonarSources = config.sonarSources
	def sonarBinaries = config.sonarBinaries ?: 'target/classes'
        def sonarInclusions = config.sonarInclusions ?: '.'
	def sonarExclusions = config.sonarExclusions ?: ''
	//def sonarscanner = tool 'sonar-scanner'
	def sonarscanner = tool 'sonar_scanner'
	def sonarSourceEncoding = config.sourceEncoding ?: 'UTF-8'
	def coverageExclusions = config.coverageExclusions ?: '**/target/**'
//	docker.image('registry.docker.nat.verifone.com/devops-ee/sonarscan-image:1.0').inside {
		withSonarQubeEnv('Sonarqube-Server') {
		// If you have configured more than one global server connection, you can specify its name
      		sh """
	  	${sonarscanner}/bin/sonar-scanner -Dsonar.projectKey=${projectKey} \
		      		-Dsonar.projectName=${projectName} \
				-Dsonar.projectVersion=${projectVersion} \
				-Dsonar.sources=${sonarSources} \
				-Dsonar.language=${sonarLanguage} \
				-Dsonar.java.binaries=${sonarBinaries} \
				-Dsonar.sourceEncoding=${sonarSourceEncoding} \
				-Dsonar.sonar.coverage.exclusions=${coverageExclusions}
		"""
    	//	}
	}
	
}
