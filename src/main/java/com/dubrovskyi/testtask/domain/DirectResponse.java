package com.dubrovskyi.testtask.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

//I would suggest Lombok as must-to-use library for POJO
public class DirectResponse {

  @JsonProperty("dep_sid")
  private int depSid;//is there any particular reason why u r using primitive instead of an Object??
  @JsonProperty("arr_sid")
  private int arrSid;
  @JsonProperty("direct_bus_route")
  private boolean directBusRroute;

  public DirectResponse(int depSid, int arrSid, boolean directBusRroute) {
    this.depSid = depSid;
    this.arrSid = arrSid;
    this.directBusRroute = directBusRroute;
  }

  public int getDepSid() {
    return depSid;
  }

  public void setDepSid(int depSid) {
    this.depSid = depSid;
  }

  public int getArrSid() {
    return arrSid;
  }

  public void setArrSid(int arrSid) {
    this.arrSid = arrSid;
  }

  public boolean isDirectBusRroute() {
    return directBusRroute;
  }

  public void setDirectBusRroute(boolean directBusRroute) {
    this.directBusRroute = directBusRroute;
  }
}
