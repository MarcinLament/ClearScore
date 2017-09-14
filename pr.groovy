// import groovyx.net.http.HTTPBuilder
// import groovyx.net.http.HTTPBuilder
import groovy.json.JsonSlurper
// println(GITHUB_ACCESS_TOKEN)
// def get = new URL("https://api.github.com/repos/ClearScore/caesium-android-v2/pulls").openConnection();
// def getRC = get.getResponseCode();
// println(getRC)
// if(getRC.equals(200)) {
// 	println(get.getInputStream().getText())
// 	// def jsonSlurper = new JsonSlurper()
// 	// def object = jsonSlurper.parseText(get.getInputStream().getText())
//  //    println(object.number);
// }
// println html


// println(json)

// // curl -H "Authorization: token 3d91e952b529a3fee902bbec8a77acdd80db1f63" https://api.github.com/repos/ClearScore/caesium-android-v2/pulls



// def http = new HTTPBuilder()
 
// http.request( 'https://api.github.com', GET, TEXT ) { req ->
//   uri.path = '/repos/ClearScore/caesium-android-v2/pulls'
//   headers.'Authorization' = "token "
 
//   response.success = { resp, reader ->
//     assert resp.statusLine.statusCode == 200
//     println "Got response: ${resp.statusLine}"
//     println "Content-Type: ${resp.headers.'Content-Type'}"
//     println reader.text
//   }
 
//   response.'404' = {
//     println 'Not found'
//   }
// }

println getBranchNameFromPR("7")

def getBranchNameFromPR(String prNumber) {
	def github = "3d91e952b529a3fee902bbec8a77acdd80db1f63"
	// def url = "https://api.github.com/repos/ClearScore/caesium-android-v2/pulls"
	def url = "https://api.github.com/repos/MarcinLament/ClearScore/pulls"

	def json = url.toURL().getText(requestProperties: [Authorization: 'token ' + github])
	def jsonSlurper = new JsonSlurper()
	def object = jsonSlurper.parseText(json)

	def result = null
	object.find { 
	    if (String.valueOf(it.number) == prNumber) {
	    	result = it.head.ref
	    	return true
	    }
	    println it.number  // do the stuff that you wanted to before break
	    return false
	}
	return result
}