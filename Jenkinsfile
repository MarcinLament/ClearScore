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
					"Unit Test": {
						echo 'Unit testing...'
					},
					"Instrumental Test": {
						echo 'Android instumental testing...'
					}
				)
		
		}
		stage('PR Review') {
			echo 'Waiting for the input...'
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
