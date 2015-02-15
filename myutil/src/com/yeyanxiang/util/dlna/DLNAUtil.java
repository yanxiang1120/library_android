package com.yeyanxiang.util.dlna;

import java.util.List;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnPStatus;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.util.CommonLog;
import org.cybergarage.util.LogFactory;

/**
 * @author 叶雁翔
 * 
 * @Email yanxiang1120@126.com
 * 
 * @version 1.0
 * 
 * @date 2013-7-25 下午4:29:44
 * 
 * @简介
 */
public class DLNAUtil {
	private ControlPoint controlPoint;
	private boolean startflag = false;

	public DLNAUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 搜索设备
	 * 
	 * @throws Exception
	 */
	public void search() throws Exception {
		if (controlPoint == null) {
			throw new Exception("请在search之前setDeviceChangeListener");
		} else {
			if (startflag) {
				// if (searchflag) {
				// startflag = controlPoint.start();
				// } else {
				// searchflag = controlPoint.search();
				// }
				controlPoint.stop();
				startflag = controlPoint.start();
			} else {
				startflag = controlPoint.start();
			}
		}
	}

	public void stop() {
		if (controlPoint != null) {
			controlPoint.stop();
		}
	}

	public void setDeviceChangeListener(DeviceChangeListener changeListener) {
		if (controlPoint == null) {
			controlPoint = new ControlPoint();
		}

		controlPoint.addDeviceChangeListener(changeListener);
	}

	public static List<Item> getDirectory(Device selDevice) {
		CommonLog log = LogFactory.createLog();
		if (selDevice == null) {
			log.e("no selDevice!!!");
			return null;
		}

		org.cybergarage.upnp.Service service = selDevice
				.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
		if (selDevice == null) {
			log.e("no service for ContentDirectory!!!");
			return null;
		}

		// Node serverNode = service.getServiceNode();
		// if (serverNode != null){
		// serverNode.print();
		// }

		Action action = service.getAction("Browse");
		if (action == null) {
			log.e("action for Browse is null!!!");
			return null;
		}
		ArgumentList argumentList = action.getArgumentList();
		argumentList.getArgument("ObjectID").setValue(0);
		argumentList.getArgument("BrowseFlag").setValue("BrowseDirectChildren");
		argumentList.getArgument("StartingIndex").setValue("0");
		argumentList.getArgument("RequestedCount").setValue("0");
		argumentList.getArgument("Filter").setValue("*");
		argumentList.getArgument("SortCriteria").setValue("");

		// ArgumentList actionInputArgList = action.getInputArgumentList();
		// int size = actionInputArgList.size();
		// for (int i = 0; i < size; i++) {
		// Argument argument = (Argument) (actionInputArgList.get(i));
		// // argument.getArgumentNode().print();
		// }

		if (action.postControlAction()) {
			ArgumentList outArgList = action.getOutputArgumentList();
			Argument result = outArgList.getArgument("Result");

			// log.e("result value = \n" + result.getValue());

			List<Item> items = ParseUtil.parseResult(result);
			return items;
		} else {
			UPnPStatus err = action.getControlStatus();
			log.e("Error Code = " + err.getCode());
			log.e("Error Desc = " + err.getDescription());
		}
		return null;
	}

	public static List<Item> getItems(Device selDevice, String id) {
		CommonLog log = LogFactory.createLog();
		if (selDevice == null) {
			log.e("no service for ContentDirectory!!!");
			return null;
		}

		// Node selDevNode = selDevice.getDeviceNode();
		// if (selDevNode != null){
		// selDevNode.print();
		// }

		log.e("getItems id = " + id);

		org.cybergarage.upnp.Service service = selDevice
				.getService("urn:schemas-upnp-org:service:ContentDirectory:1");
		if (selDevice == null) {
			log.e("no service for ContentDirectory!!!");
			return null;
		}

		Action action = service.getAction("Browse");
		if (action == null) {
			log.e("action for Browse is null");
			return null;
		}

		// action.getActionNode().print();

		ArgumentList argumentList = action.getArgumentList();
		argumentList.getArgument("ObjectID").setValue(id);
		argumentList.getArgument("BrowseFlag").setValue("BrowseDirectChildren");
		argumentList.getArgument("StartingIndex").setValue("0");
		argumentList.getArgument("RequestedCount").setValue("0");
		argumentList.getArgument("Filter").setValue("*");
		argumentList.getArgument("SortCriteria").setValue("");

		if (action.postControlAction()) {
			ArgumentList outArgList = action.getOutputArgumentList();
			Argument result = outArgList.getArgument("Result");
			// log.e("result value = \n" + result.getValue());

			List<Item> items = ParseUtil.parseResult(result);
			return items;
		} else {
			UPnPStatus err = action.getControlStatus();
			System.out.println("Error Code = " + err.getCode());
			System.out.println("Error Desc = " + err.getDescription());
		}
		return null;
	}
}
