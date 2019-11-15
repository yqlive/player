package com.yq.player.ipfs

import java.io.Serializable

data class BandWidthInfo(
    val TotalIn: Int,
    val TotalOut: Int,
    val RateIn: Double,
    val RateOut: Double
)

data class Link(val Name: String, val Hash: String, val Size: Long, val Type: Int)

data class LinkObject(val Hash: String, val Links: List<Link>)

data class LinkObjects(val Objects: List<LinkObject>)

data class PinList(val Objects: List<LinkObject>)

data class MessageWithCode(val Message: String, val Code: Int)

data class NamedHash(val Name: String, val Hash: String)

data class NameValue(val Name: String, val Value: String)

data class ObjectStat(
    var Hash: String? = null,
    var NumLinks: Int = 0,
    var BlockSize: Long = 0,
    var LinksSize: Int = 0,
    var CumulativeSize: Long = 0,
    var DataSize: Int = 0
)

data class Path(val Path: String)

data class Peer(
    val Addr: String,
    val Peer: String,
    val Latency: String,
    val Muxer: String,
    val Streams: ArrayList<Protocol>?
)

data class PeerID(
    val ID: String,
    val PublicKey: String,
    val Addresses: List<String>,
    val AgentVersion: String,
    val ProtocolVersion: String
) : Serializable

data class Protocol(val Protocol: String)

data class Stat(@JvmField var NumObjects: Long, @JvmField var RepoSize: Long, @JvmField var StorageMax: Long, @JvmField var RepoPath: String, @JvmField var Version: String)

data class SwarmPeers(val Peers: ArrayList<Peer>?)

data class Version(
    val Version: String,
    val Commit: String,
    val System: String,
    val Golang: String,
    val Sn: SinoVersion
)

data class IORate(
    val rate: Long,
    val human: String
)

data class SinoVersion(val Version: String, val Patch: Int, val Minor: Int, val Major: Int) {
    override fun equals(other: Any?): Boolean {
        if (other != null && other is SinoVersion) {
            return other.Version == Version && other.Patch == Patch && other.Minor == Minor && other.Major == Major
        }
        return super.equals(other)
    }

    companion object {
        const val unit = 100
        fun of(code: Int): SinoVersion {
            val major: Int = code / unit / unit
            val minor: Int = (code - major * unit * unit) / unit
            val patch: Int = code - major * unit * unit - minor * unit
            val version = "$major.$minor.$patch"
            return SinoVersion(version, patch, minor, major)
        }

        fun of(version: String?): SinoVersion {
            var vCode = 0
            var vUnit = unit * unit
            version?.split(".")?.forEach {
                vCode += runCatching {
                    it.toInt() * vUnit
                }.getOrElse {
                    return@of of(0)
                }
                if (vUnit > 1)
                    vUnit /= unit
                if (vUnit < 1)
                    vUnit = 1
            }
            return of(vCode)
        }
    }
}

operator fun Int.compareTo(currentVersion: SinoVersion?): Int = SinoVersion.of(this).compareTo(currentVersion)
operator fun String.compareTo(currentVersion: SinoVersion?): Int = SinoVersion.of(this).compareTo(currentVersion)
fun SinoVersion.toInt() = Major * SinoVersion.unit * SinoVersion.unit + Minor * SinoVersion.unit + Patch

operator fun SinoVersion.compareTo(other: Any?): Int {
    return when (other) {
        is SinoVersion -> toInt().compareTo(other.toInt())
        is Int -> toInt().compareTo(other)
        is String -> {
            runCatching { other.toInt() }.getOrElse {
                if (Version == other) 0 else toInt()
            }
        }
        else -> toInt()
    }
}

operator fun SinoVersion.compareTo(other: Int?) = toInt().compareTo(other ?: 0)
operator fun SinoVersion.compareTo(other: SinoVersion?) = compareTo(other?.toInt())
operator fun SinoVersion.compareTo(other: String?) = compareTo(SinoVersion.of(other))