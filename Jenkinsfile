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
		def passedAutomatedTests = true
		parallel (
			"Unit Tests" : { 
				node { 
					deleteDir()
					unstash 'repo'
					try {
						// fastlane('unitTest')
					} catch (ex) {
						passedAutomatedTests = false
					}
				// 	step([$class: "JUnitResultArchiver", testResults: "app/build/test-results/release/TEST-*.xml"])
				} 
			},
			"Instrumented Tests" : { 
				node {
					deleteDir()
					unstash 'repo'
					try {
						// fastlane('instrumentedTest')
						throw new IOException()
					} catch (ex) {
						passedAutomatedTests = false
						currentBuild.result = 'UNSTABLE'
					}
				// 	step([$class: "JUnitResultArchiver", testResults: "app/build/outputs/androidTest-results/connected/TEST-*.xml"])
				}
			}
		)

		node {
			if (passedAutomatedTests) {
				echo "ready for code review!"
			} else {
				echo "didn't pass the automated tests"
			}
		}
	}

	node {
		def codeReviewInput
		stage('PR Review') {
			codeReviewInput = askToAcceptCodeReview()
			if(codeReviewInput) {
				echo "accepted!"
			} else {
				echo "not accepted! " + codeReviewInput
				abort()
			}
		}

		def shouldDeployInput
		stage('Awaiting QA') {
			shouldDeployInput = askToDeploy()
			if (shouldDeployInput) {
				echo "should deploy!"
			} else {
				echo "should not deploy!"
				abort()
			}
		}

		stage('Deploy') {
			echo 'Publishing to Fabric...'
		}

		def manualTestingInput
		stage('Manula Testing') {
			manualTestingInput = askToAcceptManualTesting()
			if(manualTestingInput) {
				echo 'Notify developer'
			} else {
				def qaCommentsInput = askForComments()
				echo 'Failed manual testing review: ' + qaCommentsInput
				abort()
			}
		}
	}
}

def abort() {
	currentBuild.result = 'ABORTED'
    error("stopping...")
}

def askForComments() {
	return input(id: 'qa_comments', message: 'Reason for failing?', parameters: [
		[$class: 'TextParameterDefinition', defaultValue: '', description: 'Add your comments below.', name: 'failReason']
	])
}

def askToAcceptManualTesting() {
	try {
		input(id: 'manual_testing', message: 'Has passed manual testing?')
		return true
	}
	catch(Exception e) {
		return false
	} 
}

def askToDeploy() {
	try {
		input(id: 'deploy', message: 'Ready to test?')
		return true
	}
	catch(Exception e) {
		return false
	}
}

def askToAcceptCodeReview() {
	try {
		input(id: 'code_review', message: 'Do you accept the pull request?')
		return true
	}
	catch(Exception e) {
		return false
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
