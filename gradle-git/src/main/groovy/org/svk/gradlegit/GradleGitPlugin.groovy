package org.svk.gradlegit

import groovy.transform.ToString
import org.svk.version.VersionChecker

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.GradleException


class GradleGitPlugin implements Plugin<Project> {

    private static final String EXTENSION_NAME = "git"


    @Override
    void apply(Project project) {
        println("${this.class} applied")
        GitExtension extension=project.extensions.create(EXTENSION_NAME, GitExtension,project)
        extension.init()
       // def task = project.tasks.create(GenerateGitPropertiesTask.TASK_NAME, GenerateGitPropertiesTask)

       // task.setGroup(BasePlugin.BUILD_GROUP)
       // ensureTaskRunsOnJavaClassesTask(project, task)

        project.task('printProjectGitInfo') {
            group 'git'
            doLast {
               extension.info.print()
            }
        }

        project.task('assertSemVer20Compliant')
        {
            group 'versioning'
            doFirst {
                if(!VersionChecker.isSemVer20Compliant(project.version)) throw new GradleException("Project.version[${project.version}] is not SemVer2.0 compliant")
            }
        }

        project.task('assertDockerTagCompliant')
        {
            group 'versioning'
            doFirst {
                if(!VersionChecker.isDockerTagCompliant(project.version)) throw new GradleException("Project.version[${project.version}] is not docker tag compliant")
            }
        }
    }
}