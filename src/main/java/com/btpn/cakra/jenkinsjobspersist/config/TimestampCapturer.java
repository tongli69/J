package com.btpn.cakra.jenkinsjobspersist.config;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimestampCapturer {

	public Long now() {
		return System.currentTimeMillis();
	}

}
