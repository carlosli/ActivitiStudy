package org.bpmnwithactiviti.chapter4.spring;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:chapter4/spring-test-application-context.xml")
public class SpringTest {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private IdentityService identityService;
	@Autowired
	private HistoryService historyService;

	@Test
	public void simpleProcessTest() {
		System.out.println("进程实例数"+runtimeService.createProcessInstanceQuery().count());
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("isbn", 123456L);
		runtimeService.startProcessInstanceByKey("bookorder", variableMap);
		System.out.println("进程实例数"+runtimeService.createProcessInstanceQuery().count());
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("Complete order", task.getName());
		
		User user = identityService.newUser("JohnDoe"); 
		identityService.saveUser(user);
		taskService.addCandidateUser(task.getId(), "JohnDoe");
		task.setOwner("JohnDoe");
		
		System.out.println(task.getOwner());
		System.out.println(task.getAssignee());
		
		
		System.out.println("task id " + task.getId() +
		", ProcessInstanceId " + task.getProcessInstanceId() +
		", ProcessDefinitionId " + task.getProcessDefinitionId() + ", name " + task.getName() +
		", def key " + task.getTaskDefinitionKey());
		
		variableMap.put("isbn", 121231L);
		taskService.complete(task.getId(),variableMap);
//		assertEquals(0, runtimeService.createProcessInstanceQuery().count());
		
		
		HistoricProcessInstance historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		
		System.out.println("history process with definition id "
				+ historicProcessInstance.getProcessDefinitionId()
				+ ", started at " + historicProcessInstance.getStartTime()
				+ ", ended at " + historicProcessInstance.getEndTime()
				+ ", duration was "
				+ historicProcessInstance.getDurationInMillis());
				
		
		
		List<HistoricDetail> historicVariableUpdateList = historyService
				.createHistoricDetailQuery().variableUpdates().list();
		assertNotNull(historicVariableUpdateList);
		System.out.println("历史参数list长度： "+historicVariableUpdateList.size());
		for (HistoricDetail historicDetail : historicVariableUpdateList) {
			assertTrue(historicDetail instanceof HistoricVariableUpdate);
			HistoricVariableUpdate historicVariableUpdate = (HistoricVariableUpdate) historicDetail;
			assertNotNull(historicVariableUpdate.getExecutionId());
			System.out.println("historic variable update,  revision "
					+ historicVariableUpdate.getRevision()
					+ ", variable type name "
					+ historicVariableUpdate.getVariableTypeName()
					+ ", variable name "
					+ historicVariableUpdate.getVariableName()
					+ ", Variable value '" + historicVariableUpdate.getValue()
					+ "'");
		}
		
		
	}
}
