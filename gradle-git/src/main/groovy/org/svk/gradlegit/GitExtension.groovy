package org.svk.gradlegit
import org.gradle.api.Project
import org.ajoberstar.grgit.Grgit
import org.slf4j.LoggerFactory


class GitExtension{
    private def _repo
    private GitInfo _info
    private Project _project

    GitExtension(Project project)
    {
        this._project=project
    }

    public String[] versionTagPatterns = ['[0-9]*','v[0-9]*']
    public Closure  versionTemplate={ it -> 
            String version="${it.version_tag_clean?:'0.0.0'}"
            if ('0' != it.version_tag_distance) {
                    version+=   "-${it.version_tag_distance}"+
                                "-${it.commit_abbr_id}"+
                                "-${it.branch_short}"
                                }
            version+ "-${(it.clean)?'':'dirty'}"
            }
    public Closure versionTagCleanup={it -> it.find(/\d.+$/)}

    public GitInfo getInfo(){return _info}
    public GitInfo getRepo(){return _repo}

    void init(){
        _repo=Grgit.open(currentDir:repoFolder());
        _info=new GitInfo(this,_repo)
        _info.fetch();
    }

    private   File repoFolder() {
        //File repoFolder = project.file(gitProperties.dotGitDirectory) : null
        //return new GitDirLocator(project.projectDir).lookupGitDirectory(dotGitDirectory)
        return _project.projectDir
    }
}