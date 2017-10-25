import groovy.json.JsonSlurper

env.SOURCE_BRANCH_NAME = env.BRANCH_NAME
// env.GITHUB_REPO = "caesium-android-v2"
// env.GITHUB_REPO_OWNER = "ClearScore"
env.GITHUB_REPO = "ClearScore"
env.GITHUB_REPO_OWNER = "MarcinLament"

def hasOpenPullRequest = false
node {
	withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: env.GITHUB_USER_ID, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
		env.GITHUB_ACCESS_TOKEN = PASSWORD
		if (env.BRANCH_NAME.toLowerCase().startsWith('pr-')) {
			hasOpenPullRequest = true
			// env.SOURCE_BRANCH_NAME = getBranchNameFromPR(env.CHANGE_ID)
		}
	}
}

def branch_type = getBranchType(env.SOURCE_BRANCH_NAME)
echo "branch_type: $branch_type"

// ==================================== FEATURE PIPELINE ==================================== //
if ((branch_type == "feature" || branch_type == "bug") && hasOpenPullRequest)  {

	node {
		stage('Checkout') {
			deleteDir()
			checkout scm
			env.CHANGE_COMMIT_ID = getLatestCommitIdForPR()
			sh 'printenv'
			fastlane('ensureCheckout parent_branch:develop')
		}
	}

	stage('Unit Test') {
		node {
			try {
				fastlane('unitTest')
				publishUnitTestReport()
			} catch (ex) {
				publishUnitTestReport()
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

/*
 * Publishes Unit Test report so it can be reviewed in the Tests tab.
 */
def publishUnitTestReport(){
	try {
		step([$class: "JUnitResultArchiver", testResults: "ClearScore/app/build/test-results/testAlphaUnitTest/TEST-*.xml"])
	} catch (Exception e) {
		echo "Test results are missing."
		currentBuild.result  = 'UNSTABLE'
	}
}

/*
 * Aborts pipeline job.
 */
def abort() {
	currentBuild.result = 'ABORTED'
    error("stopping...")
}

/*
 * Retrieves branch name from GitHub based on the Pull Request number.
 */
def getBranchNameFromPR(String prNumber) {
	def header = [Authorization: 'token ' + env.GITHUB_ACCESS_TOKEN]
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

/*
 * Executes fastlane task.
 */
def fastlane(String lane) {
	// caesium-android-v2 project is nested in the ClearScore folder. Beacuse of that 
	// we need to change directory before executing the command.
	def pwd = sh "pwd | xargs basename"
	def command = "bundle exec fastlane " + lane
	if (pwd != "ClearScore") {
		command = "cd ClearScore && " + command
	}
	sh command
}

/*
 * Returns branch type based on the branch name.
 */
def getBranchType(String branchName) {
    def dev_pattern = ".*develop"
    def release_pattern = ".*release/.*"
    def bug_pattern = ".*bug/.*"
    def feature_pattern = ".*feature/.*"
    def hotfix_pattern = ".*hotfix/.*"
    def master_pattern = ".*master"
    if (branchName =~ dev_pattern) {
        return "develop"
    } else if (branchName =~ release_pattern) {
        return "release"
    } else if (branchName =~ bug_pattern) {
        return "bug"
    } else if (branchName =~ master_pattern) {
        return "master"
    } else if (branchName =~ feature_pattern) {
        return "feature"
    } else if (branchName =~ hotfix_pattern) {
        return "hotfix"
    } else {
        return null;
    }
}

/*
 * Returns incremented build number from the env.BUILD_NUMBER_SERVER_URL server.
 */
def getNextBuildNumber(String branchType) {
	def url = env.BUILD_NUMBER_SERVER_URL + "/${env.GITHUB_REPO}/${branchType}"
	return url.toURL().text
}

/*
 * Returns the latest change commit sha for the Pull Request. Note this is not the latest HEAD commit 
 * because Github creates a separate Merge commit for the Pull Request.
 */
def getLatestCommitIdForPR() {
	def commitId = sh script: 'git log -n 1 --skip 1 --pretty=format:"%H"', returnStdout: true
	return commitId.trim();
}
