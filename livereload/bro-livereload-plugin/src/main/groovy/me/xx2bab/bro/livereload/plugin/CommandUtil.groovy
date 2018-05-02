package me.xx2bab.bro.livereload.plugin

class CommandUtil {

    static runCommand(String command) {
        def proc = command.execute()
        proc.in.eachLine { line -> SLog.i(line) }
        proc.out.close()
        proc.waitFor()

        if (proc.exitValue()) {
            // println("${proc.getErrorStream()}")
        }
    }

    static String runCommandWithResultBack(String command) {
        def proc = command.execute()
        def out = new StringBuilder(), err = new StringBuilder()
        proc.waitForProcessOutput(out, err)
        if (err.toString().trim() == '') {
            return out.toString()
        } else {
            return null
        }
    }

}
