module edgar.core {
    requires org.apache.httpcomponents.httpcore;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires io.vavr;
    requires org.apache.httpcomponents.httpclient;
    requires org.slf4j;

    exports com.github.rskupnik.edgar;
    exports com.github.rskupnik.edgar.config.device;
    exports com.github.rskupnik.edgar.db;
    exports com.github.rskupnik.edgar.db.entity;
    exports com.github.rskupnik.edgar.db.repository;
    exports com.github.rskupnik.edgar.domain;
    exports com.github.rskupnik.edgar.tts;
}