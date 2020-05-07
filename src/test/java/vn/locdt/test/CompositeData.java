package vn.locdt.test;

public class CompositeData {
    private Integer seqNo;
    private Data data;

    public Integer getSeqNo() {
        return this.seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Double qty;
        private String name;

        public Double getQty() {
            return this.qty;
        }

        public void setQty(Double qty) {
            this.qty = qty;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}


