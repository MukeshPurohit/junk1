def call(body)
{
	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
    	body.delegate = config
    	body()
    	def mavenGoals = config.mavenGoals
    	//def M3_HOME = tool 'M3_HOME_LOCATION'
    	def M3_HOME = tool 'maven'
    	// docker.image('registry.docker.nat.verifone.com/devops-ee/maven-image:1.0').inside {
       		sh """
         	${M3_HOME}/bin/mvn ${mavenGoals}
        	"""
    	//}
}
