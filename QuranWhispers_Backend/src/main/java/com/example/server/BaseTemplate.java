package com.example.server;

import java.sql.SQLException;

abstract public class BaseTemplate {
    abstract public void PUSH();
    abstract public void GET();
    abstract public void DELETE();
    abstract public void UPDATE();
}
