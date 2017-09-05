def branch_type = get_branch_type "${env.BRANCH_NAME}"

node {
	if (branch_type == "master") {

		stage('MASTER') {
			echo 'Master pipeline'
			sh "id -un"
			sh "ruby -v"
			sh "bundle"
		}
	}

	if (branch_type == "feature") {
		stage('Checkout') {
			echo 'Checking out code'
			checkout scm
			fastlane('ensureCheckout')
		}
		stage('Test') {
			parallel(
				"Unit Tests": {
					echo 'Unit testing...'
					// fastlane('unitTest')
					// sh "Getting test test-results: ${env.WORKSPACE}/app/build/test-results/release/*.xml"
					// junit '/app/build/test-results/release/TEST-*.xml'
					// publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: false, reportDir: '/app/build/reports/tests/release', reportFiles: 'index.html', reportName: 'Unit Test Report', reportTitles: ''])
				},
				"Instrumented Tests": {
					echo 'Android instrumented testing...'
				}
			)
		}
		def userInput
		stage('PR Review') {
			userInput = input(
				id: 'Proceed1', message: 'Pull Request', parameters: [
				[$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Accept Pull Request??']
				])
		}
		if(userInput) {
			stage('Deploy') {
				echo 'Publishing to Fabric...'
			}
			
			def manualTestingResult
			stage('Manual Testing') {
				manualTestingResult = input(
				id: 'Proceed2', message: 'Manual testing', parameters: [
				[$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Has passed manual testing??']
				])
			}
			if(manualTestingResult) {
				echo 'Notify developer'
			} else {
				def manualTestingComments = input(
				 id: 'Proceed3', message: 'Reason for failing?', parameters: [
				 [$class: 'TextParameterDefinition', defaultValue: '', description: 'Reason', name: 'failReason']
				])
				echo 'Failed manual testing review: ' + manualTestingComments
				currentBuild.result = 'FAILURE'
			}
		} else {
			echo 'Failed code review...'
			currentBuild.result = 'FAILURE'
		}

		// junit '/app/build/test-results/release/TEST-*.xml'
		junit '/Users/Shared/Jenkins/TEST-1.xml'
	}
}

// Utility functions
def fastlane(String command) {
	sh "bundle exec fastlane " + command
}

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
