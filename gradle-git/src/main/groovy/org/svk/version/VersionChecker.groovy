
package org.svk.version
import java.util.regex.Pattern

class VersionChecker {
    static Pattern patternSemVer20= Pattern.compile(/^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?$/)
    
    static Boolean isSemVer20Compliant(String versionString){
        return  versionString.matches(patternSemVer20)
    }

    static Pattern patternDockerTag = Pattern.compile(/^[0-9a-zA-Z][0-9a-zA-Z-_\.]+$/)
    static Boolean isDockerTagCompliant(String versionString){
        return  versionString.matches(patternDockerTag)
    }

}