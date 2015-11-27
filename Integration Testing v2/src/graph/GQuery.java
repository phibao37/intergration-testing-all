package graph;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * Thao tác trên một danh sách các đối tượng đồ họa
 */
public class GQuery {
	
	private ArrayList<Component> mData;
	
	private GQuery() {
		mData = new ArrayList<>();
	}
	
	/**
	 * Thêm các đối tượng đồ họa vào danh sách
	 */
	public GQuery add(Component... components){
		for (Component c: components)
			mData.add(c);
		return this;
	}
	
	/**
	 * Tạo một danh sách rỗng
	 */
	public static GQuery blank(){
		return new GQuery();
	}
	
	/**
	 * Tạo một danh sách với một đối tượng gốc
	 * @param root đối tượng đồ họa gốc
	 * @return danh sách hiện thời
	 */
	public static GQuery root(Component root){
		return blank().add(root);
	}
	
	/**
	 * Tạo một danh sách với một danh sách các đối tượng
	 * @param components danh sách đối tượng
	 */
	public static GQuery from(Component... components){
		GQuery b = blank();
		
		for (Component c: components)
			b.add(c);
		return b;
	}
	
	/**
	 * Lọc các đối tượng trong danh sách (và các đối tượng con của nó)
	 * thỏa mãn một điều kiện, trả về danh sách mới 
	 * @param filter
	 * @return danh sách mới đã lọc
	 */
	public GQuery find(Object filter){
		GQuery build = blank();
		
		for (Component c: mData)
			traverse(Filter.valueOf(filter), c, build);
		
		return build;
	}
	
	private void traverse(Filter f, Component c, GQuery b){
		if (f.accept(c))
			b.add(c);
		if (c instanceof Container){
			for (Component child: ((Container)c).getComponents())
				traverse(f, child, b);
		}
	}
	
	/**
	 * Kích hoạt hoặc chặn các đối tượng trong danh sách
	 * @param enable kích hoạt đối tượng
	 */
	public GQuery enabled(boolean enable){
		for (Component c: mData)
			c.setEnabled(enable);
		return this;
	}
	
	/**
	 * Đặt tên nhóm cho các đối tượng trong danh sách
	 * @param group tên nhóm
	 */
	public GQuery group(String group){
		for (Component c: mData)
			if (c instanceof JComponent){
				((JComponent)c).putClientProperty(GROUP, group);
			}
		return this;
	}

	private static final String GROUP = "group";
	
	/**
	 * Tạo đối tượng để lọc kết quả danh sách
	 */
	public static abstract class Filter{
		
		/**
		 * Có chấp nhận 1 đối tượng hay không
		 */
		private boolean accept(Component c){
			boolean accept = with(c) && withName(c.getName());
			
			if (accept && c instanceof JComponent){
				accept = withGroup((String) 
						((JComponent)c).getClientProperty(GROUP));
			}
			
			return accept;
		}
		
		/**
		 * Lọc theo nhóm đã được gán cho đối tượng
		 * @param group tên nhóm
		 * @return <i>true</i> nếu nhóm này được chấp nhận
		 */
		public boolean withGroup(String group){
			return true;
		}
		
		/**
		 * Lọc theo tên đã được gán cho đối tượng
		 * @param name tên đối tượng
		 * @return <i>true</i> nếu tên này được chấp nhận
		 */
		public boolean withName(String name){
			return true;
		}
		
		/**
		 * Lọc theo đối tượng cụ thể
		 * @param c đối tượng đồ họa cần duyệt
		 * @return <i>true</i> nếu đối tượng này được chấp nhận
		 */
		public boolean with(Component c){
			return true;
		}
		
		/**
		 * Trả về đối tượng lọc tùy theo định dạng đối tượng
		 */
		private static Filter valueOf(Object filter){
			Filter f = null;
			
			if (filter instanceof String) {
				String s = (String) filter;

				if (s.charAt(0) == '.')
					f = new Filter() {

						@Override
						public boolean withGroup(String cls) {
							return s.substring(1).equals(cls);
						}
					};
				else
					f = new Filter() {

						@Override
						public boolean withName(String name) {
							return s.equals(name);
						}
					};
			}
			
			else if (filter instanceof Filter){
				f = (Filter) filter;
			}
			
			else if (filter instanceof Component){
				f = new Filter(){

					@Override
					public boolean with(Component c) {
						return filter == c;
					}
					
				};
			}
			
			return f;
		}
		
	}
	
}
