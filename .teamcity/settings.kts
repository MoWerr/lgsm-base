import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

project {

    buildType(BuildStable)
}

open class BuildDockerImage(projectName: String, buildName: String, vcsRoot: VcsRoot, dockerPath: String) : BuildType({
    val id: String = "${projectName}_${buildName}";
    id (id.toExtId())

    name = buildName

    vcs {
        this.root(vcsRoot)
    }

    steps {
        dockerCommand {
            name = "Build image"
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                namesAndTags = dockerPath
                commandArgs = "--pull"
            }
            param("dockerImage.platform", "linux")
        }

        dockerCommand {
            name = "Push image"
            commandType = push {
                namesAndTags = dockerPath
            }
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }
})

object BuildStable : BuildDockerImage("Root", "Build", DslContext.settingsRoot, "mowerr/lgsm-base:latest")