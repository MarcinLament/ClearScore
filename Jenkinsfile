def branch_type = get_branch_type "${env.BRANCH_NAME}"

node {
	if (branch_type == "master") {

		stage('MASTER') {
			echo 'Master pipeline'
		}
	}

	if (branch_type == "feature") {
		stage('Checkout') {
			echo 'Checking out code'
		}
		stage('Test') {
			parallel(
				"Unit Tests": {
					echo 'Unit testing...'
				},
				"Instrumented Tests": {
					echo 'Android instrumented testing...'
				}
			)
		}
		def userInput
		stage('PR Review') {
			userInput = input(
				id: 'Proceed1', message: 'Accept Pull Request?', parameters: [
				[$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Accept?']
				])
			
		}
		if(userInput) {
			stage('Deploy') {
				echo 'Publishing to Fabric...'
			}	
		} else {
			echo 'Failed code review...'
			currentBuild.result = 'FAILURE'
		}
	}
}

// Utility functions
def get_branch_type(String branch_name) {
    //Must be specified according to <flowInitContext> configuration of jgitflow-maven-plugin in pom.xml
    def dev_pattern = ".*development"
    def release_pattern = ".*release/.*"
    def feature_pattern = ".*feature/.*"
    def hotfix_pattern = ".*hotfix/.*"
    def master_pattern = ".*master"
    if (branch_name =~ dev_pattern) {
        return "dev"
    } else if (branch_name =~ release_pattern) {
        return "release"
    } else if (branch_name =~ master_pattern) {
        return "master"
    } else if (branch_name =~ feature_pattern) {
        return "feature"
    } else if (branch_name =~ hotfix_pattern) {
        return "hotfix"
    } else {
        return null;
    }
}
