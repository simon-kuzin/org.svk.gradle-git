package org.svk.gradlegit
import org.slf4j.LoggerFactory


class GitInfo
{
   //PUBLIC FIELDS ==========================
    public Boolean  clean
    public String   branch
    public String   branch_short
    public String   tag
    public String   tag_distance
    public String   version_tag
    public String   version_tag_clean
    public String   version_tag_distance
    public String   commit_id
    public String   commit_abbr_id
    public String   author_name
    public String   author_email
    public String   full_message
    public String   short_message
    public List     tags
    public String   build_descriptor
    //=================================
   
    private GitExtension extension
    private def repo

    GitInfo(GitExtension extension, def repo)
    {
        super()
        this.extension=extension
        this.repo=repo
    }

    void print()
    {
        this.class.fields.each { field ->
                if (!field.name.matches('__.*')) println "${field.name.padRight(24)}:${field.get(this)}"
            } 
    }
   
    
    private String[] parseGitDescribe(String description)
    {
        if (!description) return ['','']
        def description_matcher= description =~ /(.*)-([^-]+)-g([^-]+)$/
        return [
                description_matcher[0][1]/*tag*/
                ,description_matcher[0][2] /*tag_distance*/
                ]
    }

    void fetch()
    {
        if (!repo) return;
        if (!repo.repository.jgit.repository.resolve('HEAD')) return;

        branch=getBranchName()
        branch_short=(branch)?branch.find(/[^\/]+$/):null

        clean=repo.status().clean
        
        repo.head().with {
            commit_id=id
            commit_abbr_id=abbreviatedId
            full_message=fullMessage
            short_message=shortMessage
            author_name=author.name
            author_email=author.email
        }
        
        tags=repo.tag.list().findAll { it.commit == repo.head() }.collect {it.name}

        String description=repo.describe(longDescr: true)
        (tag,tag_distance)=parseGitDescribe(description)
        
        description=repo.describe(longDescr: true,match: extension.versionTagPatterns)
        (version_tag,version_tag_distance)=parseGitDescribe(description)

        if (version_tag){
            version_tag_clean= extension.versionTagCleanup.doCall(version_tag)?:''
        } else version_tag_clean=''

        String genVersion=extension.versionTemplate.doCall(this);

        build_descriptor=(genVersion)?
                               genVersion.replaceAll(/([^a-zA-Z0-9])[^a-zA-Z0-9]+/,'$1').replaceAll(/[^a-zA-Z0-9]+$/,'')
                               :null
    }
    
    private def branchEnvs = [

        'JOB_NAME' : ['GIT_LOCAL_BRANCH', 'GIT_BRANCH', 'BRANCH_NAME'], // jenkins/hudson

        'TRAVIS' : ['TRAVIS_BRANCH'], // TravisCI https://docs.travis-ci.com/user/environment-variables/#default-environment-variables

        'TEAMCITY_VERSION' : ['teamcity.build.branch'], // https://confluence.jetbrains.com/display/TCD9/Predefined+Build+Parameters

        'GITLAB_CI' : ['CI_COMMIT_REF_NAME'], // https://docs.gitlab.com/ee/ci/variables/#predefined-variables-environment-variables

        'BAMBOO_BUILDKEY' : ['BAMBOO_PLANREPOSITORY_BRANCH'] //https://confluence.atlassian.com/bamboo/bamboo-variables-289277087.html

        ]
    
    private String getBranchName()
    {
        String branchName = repo.branch.current().name
        if (!branchName){
            Map<String, String> env = getEnv()
            outer: for ( e in branchEnvs ) {
                if (env.containsKey(e.key)) {
                    for (String name in e.value) {
                        if (env.containsKey(name)) {
                            branchName = env[name]
                            break outer
                        }
                    }
                }
            }
        }

        return branchName
    }

}