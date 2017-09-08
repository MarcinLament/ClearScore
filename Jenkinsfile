def branch_type = get_branch_type "${env.BRANCH_NAME}"

if (branch_type == "master") {

	stage('MASTER') {
		echo 'Master pipeline'
		sh "id -un"
		sh "ruby -v"
		sh "bundle"
	}
}

if (branch_type == "feature") {

	node {
		stage('Checkout') {
			deleteDir()
			checkout scm
			// fastlane('ensureCheckout')
			stash name: 'repo', useDefaultExcludes: false
		}
	}

	stage('Test') {
		parallel (
			"Unit Tests" : { 
				node { 
					deleteDir()
					unstash 'repo'
				// 	try {
				// 		fastlane('unitTest')
				// 	} catch (ex) {}
				// 	step([$class: "JUnitResultArchiver", testResults: "app/build/test-results/release/TEST-*.xml"])
				} 
			},
			"Instrumented Tests" : { 
				node {
					deleteDir()
					unstash 'repo'
				// 	try {
				// 		fastlane('instrumentedTest')
				// 	} catch (ex) {}
				// 	step([$class: "JUnitResultArchiver", testResults: "app/build/outputs/androidTest-results/connected/TEST-*.xml"])
				}
			}
		)
	}

	node {
		def codeReviewInput
		stage('PR Review') {
			codeReviewInput = askToAcceptCodeReview()
			if(codeReviewInput) {
				echo "accepted!"
			} else {
				echo "not accepted!"
				currentBuild.result = 'FAILURE'
			}
		}

		def shouldDeployInput
		stage('Awaiting QA') {
			shouldDeployInput = askToDeploy()
			if (shouldDeployInput) {
				echo "should deploy!"
			} else {
				echo "should not deploy!"
				currentBuild.result = 'FAILURE'
			}
		}

		stage('Deploy') {
			echo 'Publishing to Fabric...'
		}

		def manualTestingInput = askToAcceptManualTesting()
		stage('Manula Testing') {
			if(manualTestingInput) {
				echo 'Notify developer'
			} else {
				def manualTestingComments = askForComments()
				echo 'Failed manual testing review: ' + manualTestingComments
				currentBuild.result = 'FAILURE'
			}
		}
	}
}

def askForComments() {
	return input(message: 'Reason for failing?', parameters: [
		$class: 'TextParameterDefinition', defaultValue: '', description: 'Add you comments below:', name: 'failReason']
	)
}

def askToAcceptManualTesting() {
	return input(id: 'manual_testing', message: 'Has passed manual testing?')
}

def askToDeploy() {
	return input(id: 'deploy', message: 'Ready to deploy to Fabric?')
}

def askToAcceptCodeReview() {
	return input(id: 'code_review', message: 'Do you accept the pull request?')
	// return input(
	// 	id: 'code_review', message: 'Pull Request Review', parameters: [
	// 	[$class: 'BooleanParameterDefinition', defaultValue: true, description: 'Select the box if you accept this pull request.', name: 'I accept it!']
	// ])
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
