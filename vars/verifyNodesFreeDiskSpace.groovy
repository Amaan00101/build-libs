def agentmonitor(minDiskSpace, emailList) {
    def monitor = new monitor.AgentMonitor()
    def lowDiskFreeSpaceNodes = monitor.findAllNodesWithLowDiskFreeSpace(minDiskSpace)
    println(lowDiskFreeSpaceNodes)
    if (lowDiskFreeSpaceNodes.size() > 0) {
        def sub = "ALERT: Low disk space on build systems"
        def details = monitor.generateDescriptionForLowDiskFreeSpaec(minDiskSpace, lowDiskFreeSpaceNodes)
        emailext(mimeType: 'text/html', subject: sub, body: details, to: emailList)
    }
}