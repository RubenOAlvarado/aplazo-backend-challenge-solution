CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sequential_id BIGSERIAL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    second_last_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT valid_age CHECK (
        EXTRACT(YEAR FROM AGE(date_of_birth)) BETWEEN 18 AND 65
    )
);

CREATE TABLE credit_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    total_credit_amount NUMERIC(15,2) NOT NULL CHECK (total_credit_amount > 0),
    available_credit_amount NUMERIC(15,2) NOT NULL CHECK (available_credit_amount >= 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CLOSED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    credit_line_id UUID NOT NULL REFERENCES credit_lines(id) ON DELETE CASCADE,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    total_amount NUMERIC(15,2) NOT NULL CHECK (total_amount > amount),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'LATE', 'COMPLETED')),
    payment_scheme VARCHAR(20) NOT NULL CHECK (payment_scheme IN ('SCHEME_1', 'SCHEME_2')),
    interest_rate NUMERIC(5,2) NOT NULL,
    commission_amount NUMERIC(15,2) NOT NULL CHECK (commission_amount > 0),
    purchase_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loan_id UUID NOT NULL REFERENCES loans(id) ON DELETE CASCADE,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    scheduled_payment_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('NEXT', 'PENDING', 'ERROR')),
    installment_number INT NOT NULL CHECK (installment_number BETWEEN 1 AND 5),

    UNIQUE (loan_id, installment_number)
);

CREATE INDEX idx_customers_sequential_id ON customers(sequential_id);
CREATE INDEX idx_customers_first_name ON customers(first_name);
CREATE INDEX idx_credit_lines_customer_id ON credit_lines(customer_id);
CREATE INDEX idx_loans_credit_line_id ON loans(credit_line_id);
CREATE INDEX idx_installments_loan_id ON installments(loan_id);
