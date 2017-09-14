import groovy.json.JsonSlurper

// def branchName = "${env.BRANCH_NAME}"
env.SOURCE_BRANCH_NAME = env.BRANCH_NAME
env.GITHUB_REPO = "ClearScore"
env.GITHUB_REPO_OWNER = "MarcinLament"

node {
	if (env.BRANCH_NAME.toLowerCase().startsWith('pr-')) {
		println "Getting branch name for PR"
		// withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'dbe24fc6-b38e-4957-81db-d1f242ed0911',
		// usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
		// 	sh 'echo uname=$USERNAME pwd=$PASSWORD'
			env.SOURCE_BRANCH_NAME = getBranchNameFromPR("e4b16f115b6ecd003099d312cd29adf090c52e4d", env.CHANGE_ID)
		// }
	}
}

def branch_type = get_branch_type(env.SOURCE_BRANCH_NAME)

// ==================================== FEATURE PIPELINE ==================================== //
if (branch_type == "feature" || branch_type == "bug") {

	node {
		stage('Checkout') {
			deleteDir()
			checkout scm
			fastlane('ensureCheckout parent_branch:develop')

			sh 'printenv'
			stash name: 'repo', useDefaultExcludes: false
		}
	}

	stage('Test') {
		def hasHandledFailure = false
		parallel (
			"Unit Tests" : { 
				node { 
					deleteDir()
					unstash 'repo'
					try {
						fastlane('unitTest')
						publishUnitTestReport()
						// throw new IOException()
					} catch (ex) {
						publishUnitTestReport()
						if (!hasHandledFailure) {
							hasHandledFailure = true
							fastlane('finalizeAutomatedTestingStage success:false')
						}
						abort()
					}
				} 
			},
			"Instrumented Tests" : { 
				node {
					deleteDir()
					unstash 'repo'
					try {
						// fastlane('instrumentedTest')
						// publishUnitTestReport()
					} catch (ex) {
						publishUnitTestReport()
						if (!hasHandledFailure) {
							hasHandledFailure = true
							fastlane('finalizeAutomatedTestingStage success:false')
						}
						abort()
					}
				}
			}
		)

		node {
			fastlane('finalizeAutomatedTestingStage success:true')
		}
	}

	node {
		def codeReviewInput
		stage('PR Review') {
			codeReviewInput = askToAcceptCodeReview()
			if(codeReviewInput) {
				fastlane('finalizeCodeReviewStage success:true')
			} else {
				abort()
			}
		}

		def shouldDeployInput
		stage('Awaiting QA') {
			shouldDeployInput = askToDeploy()
			if (!shouldDeployInput) {
				abort()
			}
		}

		stage('Deploy') {
			fastlane('deployToFabric parent_branch:develop')
		}

		def manualTestingInput
		stage('Manual Testing') {
			manualTestingInput = askToAcceptManualTesting()
			if(manualTestingInput) {
				fastlane('finalizeManualTestingStage success:true')
			} else {
				def reasonInput = askForComments()
				fastlane('finalizeManualTestingStage success:false reason:' + reasonInput)
				abort()
			}
		}
	}
}
// ==================================== DEVELOP PIPELINE ==================================== //
else if (branch_type == "develop") {

}


// ==================================== RELEASE PIPELINE ==================================== //
else if (branch_type == "release" || branch_type == "hotfix") {
	
}


// ========================================= UTILS ========================================== //
def publishUnitTestReport(){
	try {
		step([$class: "JUnitResultArchiver", testResults: "app/build/test-results/release/TEST-*.xml"])
	} catch (Exception e) {
		echo "Test results are missing."
		currentBuild.result  = 'UNSTABLE'
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
		input(id: 'code_review', message: 'Has passed the review?')
		return true
	}
	catch(Exception e) {
		return false
	}
}

// Utility functions
def getBranchNameFromPR(String token, String prNumber) {
	println("XXX: $token | xxx: $env.PASSWORD")
	def header = [Authorization: 'token $token']
	// def url = "https://api.github.com/repos/ClearScore/caesium-android-v2/pulls"

	// def header = [:]
	def url = "https://api.github.com/repos/$env.GITHUB_REPO_OWNER/$env.GITHUB_REPO/pulls"

	def json = url.toURL().getText(requestProperties: header)
	def jsonSlurper = new JsonSlurper()
	def object = jsonSlurper.parseText(json)

	for (Object item : object) {
		if (String.valueOf(item.number) == prNumber) {
			return item.head.ref
		}
	}

	return null
}

def fastlane(String command) {
	sh "bundle exec fastlane " + command
}

def get_branch_type(String branch_name) {
    //Must be specified according to <flowInitContext> configuration of jgitflow-maven-plugin in pom.xml
    def dev_pattern = ".*develop"
    def release_pattern = ".*release/.*"
    def bug_pattern = ".*bug/.*"
    def feature_pattern = ".*feature/.*"
    def hotfix_pattern = ".*hotfix/.*"
    def master_pattern = ".*master"
    if (branch_name =~ dev_pattern) {
        return "develop"
    } else if (branch_name =~ release_pattern) {
        return "release"
    } else if (branch_name =~ bug_pattern) {
        return "bug"
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
