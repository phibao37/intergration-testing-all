package core.inte;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

/**
 * Quản lý danh sách các bộ stub trong chương trình
 */
public class StubSuiteManager extends ArrayList<StubSuite> {
	private static final long serialVersionUID = 1L;
	private static final DefaultListModel<StubSuite> LISTEN = new DefaultListModel<>();
	private static int NAME = 0;
	
	private DefaultListModel<StubSuite> mListener = LISTEN;
	private int mSelected = -1;
	
	@Override
	public boolean add(StubSuite e) {
		mListener.addElement(e);
		ensureStubSuiteName(e);
		return super.add(e);
	}
	
	private void ensureStubSuiteName(StubSuite e){
		if (e.getName() == null || e.getName().isEmpty())
			e.setName("Bộ Stub " + NAME++);
	}
	
	/**
	 * Chọn bộ stub dùng để phục vụ kiểm thử
	 */
	public void setSelectedSuite(int selected){
		mSelected = selected;
	}
	
	/**
	 * Trả về bộ stub đang được chọn, hoặc null nếu chưa chọn bộ nào
	 */
	public StubSuite getSelectedStubSuite(){
		return mSelected == -1 ? null : get(mSelected);
	}

	/**
	 * Đặt bộ danh sách đồ họa để lắng nghe các thay đổi trong danh sách stub
	 */
	public void setListListener(DefaultListModel<StubSuite> list){
		mListener = list;
	}
}
