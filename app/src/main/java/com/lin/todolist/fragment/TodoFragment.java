package com.lin.todolist.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lin.todolist.R;
import com.lin.todolist.activity.EditTodoActivity;
import com.lin.todolist.activity.MainActivity;
import com.lin.todolist.adapter.TodoSectionAdapter;
import com.lin.todolist.bean.TodoDesBean;
import com.lin.todolist.bean.TodoListBean;
import com.lin.todolist.bean.TodoSection;
import com.lin.todolist.fragment.base.BaseFragment;
import com.lin.todolist.http.HttpUtils;
import com.lin.todolist.http.ResponseItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;


public class TodoFragment extends BaseFragment {
    private static final String KEY_IS_DONE = "is_done";
    private static final int REQUEST_CODE_EDIT_TODO = 0x110;

    @BindView(R.id.todo_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int page = 1;
    private TodoListBean mTodoListBean = new TodoListBean();
    private TodoSectionAdapter mAdapter;
    private int deletePosition = -1;
    private int donePosition = -1;

    private boolean isDone;

    public static TodoFragment newInstance(boolean isDone) {

        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_DONE, isDone);
        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            isDone = bundle.getBoolean(KEY_IS_DONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_todo;
    }

    @Override
    protected void initData() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                requestTodoListData();
            }
        });

        requestTodoListData();
    }



    private void requestTodoListData() {
        HttpUtils.requestTodoList(this, page, isDone);
    }

    private void deleteTodoById(int todoId) {
        HttpUtils.deleteTodoById(this,todoId);
    }

    private void doneTodoById(int todoId) {
        HttpUtils.doneTodoById(this, todoId, isDone ? 0 : 1);
    }

    public void updateUI(final ResponseItem<TodoListBean> response) {
        if (response.isSuccess() && response.getData() != null) {
            if (page == 1) {
                mTodoListBean = response.getData();
                initAdapter();
            } else {
                mAdapter.addData(getTodoSectionData(response.getData().getDatas()));
                mAdapter.loadMoreComplete();
            }

        }
    }

    private void initAdapter() {
        mAdapter = new TodoSectionAdapter(R.layout.todo_item_view,R.layout.todo_item_head,getTodoSectionData(mTodoListBean.getDatas()),isDone);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mTodoListBean.getPageCount() == 1) {
                    mAdapter.loadMoreEnd(true);
                } else if (page >= mTodoListBean.getPageCount()) {
                    mAdapter.loadMoreEnd(false);
                } else {
                    page++;
                    requestTodoListData();
                }
            }
        }, mRecyclerView);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.item_complete:
                        donePosition = position;
                        doneTodoById(mAdapter.getData().get(position).t.getId());
                        break;
                    case R.id.item_delete:
                        showDialog(position);
                        break;
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TodoSection todoSection = mAdapter.getData().get(position);
                //showTodoDes(todoSection);
                Bundle bundle = new Bundle();
                bundle.putSerializable("todo_des", todoSection.t);
                startActivityForResult(EditTodoActivity.class, bundle, REQUEST_CODE_EDIT_TODO);
            }
        });
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showTodoDes(TodoSection todoSection) {
        View view = View.inflate(getContext(),R.layout.dialog_todo_des_view, null);
        TextView todoName = view.findViewById(R.id.todo_name);
        todoName.setText(todoSection.t.getTitle());
        TextView todoContent = view.findViewById(R.id.todo_content);
        if (TextUtils.isEmpty(todoSection.t.getContent())) {
            todoContent.setText(R.string.no_text);
        } else {
            todoContent.setText(todoSection.t.getContent());
        }
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getContext());
        sheetDialog.setContentView(view);
        sheetDialog.show();


    }

    private List<TodoSection> getTodoSectionData(List<TodoDesBean> datas) {
        List<TodoSection> todoSections = new ArrayList<>();
        LinkedHashSet<String> dates = new LinkedHashSet<>();
        if (datas != null) {
            for (TodoDesBean todoDesBean : datas) {
                dates.add(todoDesBean.getDateStr());
            }
            for (String date : dates) {
                TodoSection todoSectionHead = new TodoSection(true, date);
                todoSections.add(todoSectionHead);
                for (TodoDesBean todoDesBean : datas) {
                    if (TextUtils.equals(date, todoDesBean.getDateStr())) {
                        TodoSection todoSectionContent = new TodoSection(todoDesBean);
                        todoSections.add(todoSectionContent);
                    }
                }
            }
        }

        return todoSections;
    }

    @Override
    protected boolean isLoadingEnable(int requestId) {
        return requestId == 1;
    }

    @Override
    public void start(int requestId) {
        if (requestId == 1) {
            super.start(requestId);
        } else {
            if (page == 1) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

    }

    @Override
    public void error(Throwable t, int requestId) {
        super.error(t,requestId);
        if (requestId == 0) {
            if (page > 1) {
                mAdapter.loadMoreFail();
            }
        }
    }

    @Override
    public void end(int requestId) {
        if (requestId == 1) {
            super.end(requestId);
        } else {
            if (page == 1) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public void updateAddTodoData(TodoDesBean todoDesBean) {
        if (mAdapter == null) {
            initAdapter();
        }
        List<TodoSection> todoSections = mAdapter.getData();
        for (int i = 0; i < todoSections.size(); i++) {
            TodoSection todoSection = todoSections.get(i);
            if (todoSection.isHeader && TextUtils.equals(todoSection.header, todoDesBean.getDateStr())) {
                TodoSection section = new TodoSection(todoDesBean);
                mAdapter.getData().add(i+1,section);
                mAdapter.notifyItemInserted(i+1);
                mRecyclerView.scrollToPosition(i+1);
                return;
            }
        }
        TodoSection sectionHead = new TodoSection(true,todoDesBean.getDateStr());
        mAdapter.getData().add(0, sectionHead);
        TodoSection section = new TodoSection(todoDesBean);
        mAdapter.getData().add(1, section);
        mAdapter.notifyItemRangeInserted(0,2);
        mRecyclerView.scrollToPosition(0);
    }

    public void updateRemovedData(ResponseItem response) {
        if (response.isSuccess() && deletePosition != -1) {
            //上一个为头,还需判断下下一个是什么
            if (mAdapter.getData().get(deletePosition-1).isHeader) {
                if (deletePosition + 1 < mAdapter.getData().size() && mAdapter.getData().get(deletePosition + 1) != null) {
                    if (mAdapter.getData().get(deletePosition + 1).isHeader) {
                        //下一个为头
                        mAdapter.getData().remove(deletePosition);
                        mAdapter.getData().remove(deletePosition-1);
                        mAdapter.notifyItemRangeRemoved(deletePosition-1,2);
                    }else{
                        mAdapter.getData().remove(deletePosition);
                        mAdapter.notifyItemRemoved(deletePosition);
                    }
                }else{
                    //没有下一个，则直接删除
                    mAdapter.getData().remove(deletePosition);
                    mAdapter.getData().remove(deletePosition-1);
                    mAdapter.notifyDataSetChanged();
                }

            } else {
                mAdapter.getData().remove(deletePosition);
                mAdapter.notifyItemRemoved(deletePosition);
            }
            showToast(getString(R.string.delete_todo_success));
        }

    }

    public void updateDoneData(ResponseItem<TodoDesBean> response) {
        if (response.isSuccess() && donePosition != -1) {
            if (mAdapter.getData().get(donePosition-1).isHeader && (mAdapter.getData().size()== donePosition+2 || mAdapter.getData().get(donePosition+1).isHeader)) {
                mAdapter.getData().remove(donePosition-1);
                mAdapter.getData().remove(donePosition-1);
                mAdapter.notifyItemRangeRemoved(donePosition-1,2);
            } else {
                mAdapter.getData().remove(donePosition);
                mAdapter.notifyItemRemoved(donePosition);
            }
            showToast(getString(isDone ? R.string.notdo_todo_success : R.string.done_todo_success));
            ((MainActivity)getActivity()).updateDoneOrCancelData((TodoDesBean)(response.getData()), isDone ? 0 : 1);
        }
    }


    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_todo);
        builder.setMessage(R.string.sure_delete_todo);
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePosition = position;
                deleteTodoById(mAdapter.getData().get(position).t.getId());
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_TODO) {
            switch (resultCode) {
                case 0x210:
                    page = 1;
                    requestTodoListData();
                    break;
            }
        }
    }
}
