package api.expression;

import api.models.IType;

public interface IObjectExpression extends IExpressionGroup {

	IExpression getMember(String member);
	
	IType getMemberType(String member);
	
	default boolean isMemberSet(String member){
		return getMember(member) != null;
	}
	
	void setMember(String name, IExpression member);
	
	@Override
	public default int _handleVisit(IExpressionVisitor visitor) {
		return visitor.visit(this);
	}


	@Override
	public default void _handleLeave(IExpressionVisitor visitor) {
		visitor.leave(this);
	}
}
