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

			// sh "$ANDROID_HOME/emulator/emulator @Nexus-5_API-25 -memory 2048 -wipe-data -no-window"
		}
		// stage('Test') {
		// 	parallel(
		// 		"Unit Tests": {
		// 			echo 'Unit testing...'
		// 			try {
		// 				fastlane('unitTest')
		// 			} catch (ex) {}
		// 			step([$class: "JUnitResultArchiver", testResults: "app/build/test-results/release/TEST-*.xml"])
		// 		},
		// 		"Instrumented Tests": {
		// 			echo 'Android instrumented testing...'
		// 			try {
		// 				fastlane('instrumentedTest')
		// 			} catch (ex) {}
		// 			step([$class: "JUnitResultArchiver", testResults: "app/build/outputs/androidTest-results/connected/TEST-*.xml"])
		// 		}
		// 	)
		// }

		stage('Test') {
			parallel (
				"Unit Tests" : { 
					node { 
						echo "pwd"
						echo "ls -a"
						echo 'Unit testing...'
						try {
							fastlane('unitTest')
						} catch (ex) {}
						step([$class: "JUnitResultArchiver", testResults: "app/build/test-results/release/TEST-*.xml"])
					} 
				},
				"Instrumented Tests" : { 
					node {
						echo 'Android instrumented testing...'
						try {
							fastlane('instrumentedTest')
						} catch (ex) {}
						step([$class: "JUnitResultArchiver", testResults: "app/build/outputs/androidTest-results/connected/TEST-*.xml"])
					}
				}
			)
		}

		// stage('Unit Tests') {
		// 	echo 'Unit testing...'
		// 	try {
		// 		fastlane('unitTest')
		// 	} catch (ex) {}
		// 	step([$class: "JUnitResultArchiver", testResults: "app/build/test-results/release/TEST-*.xml"])
		// }
		// stage('Instrumented Tests') {
		// 	echo 'Android instrumented testing...'
		// 	try {
		// 		fastlane('instrumentedTest')
		// 	} catch (ex) {}
		// 	step([$class: "JUnitResultArchiver", testResults: "app/build/outputs/androidTest-results/connected/TEST-*.xml"])

		// }
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

		// archive "/app/build/test-results/release/TEST-lammar.com.csdemo.ui.showscore.ShowScorePresenterTest.xml"
		// archiveArtifacts artifacts: '/app/build/test-results/release/TEST-lammar.com.csdemo.ui.showscore.ShowScorePresenterTest.xml', excludes: 'output/*.md'
		// junit "/app/build/test-results/release/TEST-lammar.com.csdemo.ui.showscore.ShowScorePresenterTest.xml"
		// junit "/Users/Shared/Jenkins/TEST-1.xml"
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
