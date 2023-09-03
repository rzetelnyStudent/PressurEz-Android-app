package com.MichalKapuscinski.BikeTPMS.models

enum class ProtocolVer(val id: Int) {
    OLD(0xEACA),
    NEW(0xBEEF);

    companion object {
        public fun from(findValue: Int): ProtocolVer = ProtocolVer.values().first { it.id == findValue }
    }
}

