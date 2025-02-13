// package monitor

import jenkins.model.*
import hudson.model.*
import hudson.node_monitors.*
import hudson.util.*

class AgentMonitor {

    // Method to find nodes with free disk space below the given threshold
    public Map findAllNodesWithLowDiskFreeSpace(minSpaceInGB) {
        Map mapNodeToFreeSize = [:]
        Map lowFreeSpaceNodes = [:]
        def nodesToCheck = Jenkins.instance.nodes  

        for (node in nodesToCheck) {
            try {
                def computer = node.toComputer()
                if (computer.getChannel() == null) continue

                // Get disk space of the node
                def diskSpace = DiskSpaceMonitor.DESCRIPTOR.get(computer)
                def size = 0
                if (diskSpace != null) {
                    size = diskSpace.size
                }
                def roundedSize = (size / (1024f * 1024f * 1024f)).round(2)

                // Add node to map with rounded disk space
                mapNodeToFreeSize[node.name.toString()] = roundedSize
                if (roundedSize < minSpaceInGB) {
                    lowFreeSpaceNodes[node.name.toString()] = roundedSize
                }
            } catch (e) {
                println("ERROR: exception: " + e.toString())
                e.printStackTrace()
            }
        }

        return lowFreeSpaceNodes
    }

    // Method to generate a description of nodes with low disk space
    public String generateDescriptionForLowDiskFreeSpaec(minSpaceInGB, lowFreeSpaceNodes) {
        def desc = StringBuilder.newInstance()
        if (lowFreeSpaceNodes.size() > 0) {
            desc << "<html>"
            desc << "<body>"
            desc << "<h1>Found some nodes, which have free space lower than ${minSpaceInGB}G</h1>"
            desc << "Low Free Space Nodes:"
            desc << "<table border=1>"
            lowFreeSpaceNodes.each { node, freeSpace ->
                desc << "<tr>"
                desc << "<th align='left'>${node}</th>"
                desc << "<td align='right'><font color=red>${String.format("%.2f", freeSpace)}G</font></td>"
                desc << "</tr>"
            }
            desc << "</table>"
            desc << "</body>"
            desc << "</html>"
        }

        return desc.toString()
    }
}
