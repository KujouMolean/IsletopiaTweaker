public class Test {
    public static void main(String[] args) {
        ProcessHandle.allProcesses().forEach(processHandle -> {
                    ProcessHandle.Info info = null;
                    try {
                        info = processHandle.info();
                    } catch (Exception ignore) {
                    }
                    if (info == null) {
                        return;
                    }
                    if (info.commandLine().isPresent()) {
                        String s = info.commandLine().get();
                        if (s.contains("-DServerName=server12 ")) {
                            processHandle.destroyForcibly();
                        }
                    }
                });
    }
}
