package data.parse;

import java.nio.charset.StandardCharsets;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

public class ChangeData { //vo

	private byte[] if_id;
	private byte[] name;
	private byte[] account;
	private byte[] phone;
	
	private byte[] headerByte = new byte[10]; // 길이부
	private int length; // 길이부 길이

	private String successSentence = "정상 처리되었습니다.";

	public ChangeData() {

		this.if_id = new byte[20];
		this.name = new byte[20];
		this.account = new byte[50];
		this.phone = new byte[20];
		
		this.length = 0;

	}

	public StringBuffer makeData(String if_idStr, String nameStr, String accountStr, String phoneStr) throws Exception {

		byte[] tempIf_id = if_idStr.getBytes("euc-kr");
		byte[] tempName = nameStr.getBytes("euc-kr");
		byte[] tempAcc = accountStr.getBytes("euc-kr");
		byte[] tempPhone = phoneStr.getBytes("euc-kr");

		// 인터페이스 id 영역
		StringBuffer makeBlank = new StringBuffer();
		if (if_id.length - tempIf_id.length > 0) {
			for (int i = 0; i < if_id.length - tempIf_id.length; i++) {
				makeBlank.append(" ");
			}
			makeBlank.append(if_idStr);
		} else {
			for (int i = 0; i < if_id.length; i++) {
				if_id[i] = tempIf_id[i];
			}
			makeBlank.append(new String(if_id, "euc-kr"));
		}

		// 이름
		if (name.length - tempName.length > 0) {
			for (int i = 0; i < name.length - tempName.length; i++) {
				makeBlank.append(" ");
			}
			makeBlank.append(nameStr);
		} else {
			for (int i = 0; i < name.length; i++) {
				name[i] = tempName[i];
			}
			makeBlank.append(new String(name, "euc-kr"));
		}

		// 계좌
		if (account.length - tempAcc.length > 0) {
			for (int i = 0; i < account.length - tempAcc.length; i++) {
				makeBlank.append(" ");
			}
			makeBlank.append(accountStr);
		} else {
			for (int i = 0; i < account.length; i++) {
				account[i] = tempAcc[i];
			}
			makeBlank.append(new String(account, "euc-kr"));
		}

		// 핸드폰
		if (phone.length - tempPhone.length > 0) {
			for (int i = 0; i < phone.length - tempPhone.length; i++) {
				makeBlank.append(" ");
			}
			makeBlank.append(phoneStr);
		} else {
			for (int i = 0; i < phone.length; i++) {
				phone[i] = tempPhone[i];
			}
			makeBlank.append(new String(phone, "euc-kr"));
		}
		
		
		return makeBlank;
	}
	
	public void checkReadData(String msg)throws Exception{
		
		byte[] bytes = msg.getBytes("euc-kr");
		byte[] lastSen = new byte[bytes.length-headerByte.length+length];

		for(int i=0; i<headerByte.length; i++) {
			headerByte[i] = bytes[i];
		}
		
		length = Integer.parseInt(new String(headerByte, "euc-kr"));

		int num = 0;
		for(int i=headerByte.length+length; i<bytes.length; i++) {
			lastSen[num++] = bytes[i];
		}
		
		String result = new String(lastSen, "euc-kr").trim(); // 정상 처리되었습니다.

		if(result.equals(successSentence)) {
			printData(msg);
		}else {
			System.out.println("데이터에 오류가 있습니다.");
		}

	}

	private void printData(String msg) throws Exception {

		byte[] bytes = msg.getBytes("euc-kr"); // 수신된 전체 데이터

		System.out.println("{길이부} = " + length);
		
		byte[] bodyByte = new byte[length];
		
		int su = 0;
		for(int i=headerByte.length; i<bytes.length; i++) {
			if(su < bodyByte.length) {
				bodyByte[su++] = bytes[i];
			}else {
				break;
			}
		}
		
		// data
		int j = 0;
		for(int i=0; i<bodyByte.length; i++) {
			if(j < if_id.length) {
				if_id[j++] = bodyByte[i];
			}else {
				j = 0;
				break;
			}
		}
		String if_idStr = new String(if_id, "euc-kr").trim();
		
		for(int i=if_id.length; i<bodyByte.length; i++) {
			if(j < name.length) {
				name[j++] = bodyByte[i];
			}else {
				j = 0;
				break;
			}
		}
		String nameStr = new String(name, "euc-kr").trim();
		
		for(int i=if_id.length+name.length; i<bodyByte.length; i++) {
			if(j < account.length) {
				account[j++] = bodyByte[i];
			}else {
				j = 0;
				break;
			}
		}
		String accountStr = new String(account, "euc-kr").trim();

		for(int i=if_id.length+name.length+account.length; i<bodyByte.length; i++) {
			if(j < phone.length) {
				phone[j++] = bodyByte[i];
			}else {
				j = 0;
				break;
			}
		}
		String phoneStr = new String(phone, "euc-kr").trim();
		
		
		System.out.println("{인터페이스 id} = " + if_idStr);
		System.out.println("{이름} = " + nameStr);
		System.out.println("{계좌번호} = " + accountStr);
		System.out.println("{핸드폰 번호} = " + phoneStr);
		

	}
	




}
