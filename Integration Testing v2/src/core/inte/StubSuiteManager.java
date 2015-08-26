package core.inte;

import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 * Quản lý danh sách các bộ stub trong chương trình
 */
public class StubSuiteManager extends ArrayList<StubSuite> {
	private static final long serialVersionUID = 1L;
	private static final DefaultListModel<StubSuite> LISTEN = new DefaultListModel<>();
	private DefaultListModel<StubSuite> mListener = LISTEN;
	private StubSuite mSelected;
	
	@Override
	public boolean add(StubSuite e) {
		ensureStubSuiteName(e);
		boolean a = super.add(e);
		mListener.addElement(e);
		return a;
	}

	@Override
	public StubSuite remove(int index) {
		StubSuite s = super.remove(index);
		mListener.remove(index);
		return s;
	}


	@Override
	public StubSuite set(int index, StubSuite element) {
		StubSuite s = super.set(index, element);
		mListener.set(index, element);
		return s;
	}

	/**
	 * Xóa bỏ các bộ stub tại các vị trí trong mảng
	 */
	public void removeAll(int[] selectedIndices) {
		for (int i = selectedIndices.length - 1; i >= 0; i--){
			remove(selectedIndices[i]);
		}
	}
	
	private void ensureStubSuiteName(StubSuite e){
		if (e.getName() == null || e.getName().isEmpty())
			e.setName("Bộ Stub " + (size()+1));
	}

	/**
	 * Chọn bộ stub dùng để phục vụ kiểm thử
	 */
	public void setSelectedSuite(StubSuite selected){
		mSelected = selected;
	}
	
	/**
	 * Trả về bộ stub đang được chọn, hoặc null nếu chưa chọn bộ nào
	 */
	public StubSuite getSelectedStubSuite(){
		return mSelected;
	}

	/**
	 * Đặt bộ danh sách đồ họa để lắng nghe các thay đổi trong danh sách stub
	 */
	public void setListListener(DefaultListModel<StubSuite> list){
		mListener = list;
	}
}
