package org.bpmnwithactiviti.chapter4.spring;

import org.activiti.engine.delegate.DelegateExecution;
import org.hibernate.action.Executable;

public class OrderService {
	
	public void validate(DelegateExecution execution) {
//		execution.
		System.out.println("excute OrderService.validate()");
		System.out.println("validating order for isbn " + execution.getVariable("isbn"));
	}

}
